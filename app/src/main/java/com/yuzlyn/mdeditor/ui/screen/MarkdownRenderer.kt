package com.yuzlyn.mdeditor.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.vladsch.flexmark.ast.BlockQuote
import com.vladsch.flexmark.ast.BulletList
import com.vladsch.flexmark.ast.Code
import com.vladsch.flexmark.ast.Emphasis
import com.vladsch.flexmark.ast.FencedCodeBlock
import com.vladsch.flexmark.ast.HardLineBreak
import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.ast.HtmlInline
import com.vladsch.flexmark.ast.IndentedCodeBlock
import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.ast.ListItem
import com.vladsch.flexmark.ast.OrderedList
import com.vladsch.flexmark.ast.Paragraph
import com.vladsch.flexmark.ast.SoftLineBreak
import com.vladsch.flexmark.ast.StrongEmphasis
import com.vladsch.flexmark.ast.Text as MdText
import com.vladsch.flexmark.ast.ThematicBreak
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TableBlock
import com.vladsch.flexmark.ext.tables.TableBody
import com.vladsch.flexmark.ext.tables.TableCell
import com.vladsch.flexmark.ext.tables.TableHead
import com.vladsch.flexmark.ext.tables.TableRow
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet

private const val TAG = "MDEditor_Debug"

private val flexmarkOptions: MutableDataSet by lazy {
  MutableDataSet().apply {
    set(HtmlRenderer.SOFT_BREAK, "<br />")
    set(Parser.BLANK_LINES_IN_AST, true)
  }
}

private val markdownParser: Parser by lazy {
  Parser.builder(flexmarkOptions)
          .extensions(listOf(TablesExtension.create(), StrikethroughExtension.create()))
          .build()
}

private val htmlRenderer: HtmlRenderer by lazy { HtmlRenderer.builder(flexmarkOptions).build() }

/**
 * 仅对连续空行的第2条及之后注入占位字符，
 * 保留首条空行不动，避免破坏表格等依赖空行作分隔符的语法。
 */
private fun preserveBlankLines(source: String): String {
  val lines = source.lines()
  val sb = StringBuilder()
  for (i in lines.indices) {
    val line = lines[i]
    if (i > 0) sb.append('\n')
    val prevBlank = i > 0 && lines[i - 1].isBlank()
    if (line.isBlank() && prevBlank && !(i == lines.lastIndex && line.isEmpty())) {
      sb.append("\u00A0\u200B")
    } else if (line.isBlank() && i == lines.lastIndex) {
      // 末尾空白行不追加内容
    } else {
      sb.append(line)
    }
  }
  return sb.toString()
}

/**
 * 将 $$...$$ 数学公式块转换为 fenced code block (```math ...
 * ```
 * ```)
 * ```
 */
private fun preprocessMathBlocks(source: String): String {
  val lines = source.lines()
  val sb = StringBuilder()
  var mathOpen = false
  for (i in lines.indices) {
    val line = lines[i]
    if (i > 0) sb.append('\n')
    if (line.trim() == "$$") {
      if (mathOpen) {
        sb.append("```")
        mathOpen = false
      } else {
        sb.append("```math")
        mathOpen = true
      }
    } else {
      sb.append(line)
    }
  }
  // 未闭合的 math 块自动闭合
  if (mathOpen) {
    sb.append("\n```")
  }
  return sb.toString()
}

@Composable
fun MarkdownPreview(
        markdown: String,
        textColor: Color,
        scrollState: androidx.compose.foundation.ScrollState = rememberScrollState(),
        modifier: Modifier = Modifier
) {
  Log.d(TAG, "Markdown 引擎已切換為 flexmark-java，解析預覽啟動")

  val rootNode =
          remember(markdown) {
            try {
              val preprocessed = preprocessMathBlocks(preserveBlankLines(markdown))
              markdownParser.parse(preprocessed)
            } catch (e: Exception) {
              Log.e(TAG, "Markdown 解析失敗: ${e.message}")
              null
            }
          }

  Column(
          modifier = modifier.verticalScroll(scrollState).fillMaxWidth().padding(horizontal = 16.dp)
  ) {
    if (rootNode == null || markdown.isBlank()) {
      Text(
              "預覽區域為空",
              style = MaterialTheme.typography.bodyLarge,
              color = textColor.copy(alpha = 0.38f)
      )
      return@Column
    }
    RenderAstNode(rootNode, textColor)
  }
}

@Composable
private fun RenderAstNode(node: Node, textColor: Color) {
  var child = node.firstChild
  while (child != null) {
    when (child) {
      is Heading -> RenderHeading(child, textColor)
      is Paragraph -> RenderParagraph(child, textColor)
      is FencedCodeBlock -> RenderFencedCodeBlock(child, textColor)
      is IndentedCodeBlock -> RenderIndentedCodeBlock(child, textColor)
      is BlockQuote -> RenderBlockQuote(child, textColor)
      is BulletList -> RenderBulletList(child, textColor)
      is OrderedList -> RenderOrderedList(child, textColor)
      is TableBlock -> RenderTable(child, textColor)
      is ThematicBreak -> {
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
                color = textColor.copy(alpha = 0.2f),
                modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
      }
      else -> RenderAstNode(child, textColor)
    }
    child = child.next
  }
}

@Composable
private fun RenderHeading(heading: Heading, textColor: Color) {
  val annotated = buildInlineAnnotatedString(heading, textColor)
  val style =
          when (heading.level) {
            1 -> MaterialTheme.typography.headlineSmall
            2 -> MaterialTheme.typography.titleLarge
            3 -> MaterialTheme.typography.titleMedium
            4 -> MaterialTheme.typography.titleSmall
            5 -> MaterialTheme.typography.titleSmall
            else -> MaterialTheme.typography.titleSmall
          }
  Spacer(modifier = Modifier.height(if (heading.level <= 2) 16.dp else 12.dp))
  ClickableMarkdownText(
          annotated = annotated,
          style = style,
          color = textColor,
          fontWeight = FontWeight.Bold
  )
  Spacer(modifier = Modifier.height(if (heading.level <= 2) 8.dp else 4.dp))
}

@Composable
private fun RenderParagraph(paragraph: Paragraph, textColor: Color) {
  val textContent = collectTextContent(paragraph).trim()
  if (textContent.isEmpty()) {
    // 空白行段落：只渲染间距，不显示文字
    Spacer(modifier = Modifier.height(14.dp))
    return
  }
  val annotated = buildInlineAnnotatedString(paragraph, textColor)
  Spacer(modifier = Modifier.height(8.dp))
  ClickableMarkdownText(
          annotated = annotated,
          style = MaterialTheme.typography.bodyLarge,
          color = textColor
  )
  Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun RenderFencedCodeBlock(codeBlock: FencedCodeBlock, textColor: Color) {
  val codeText = codeBlock.contentChars.unescape()
  val lang = codeBlock.info?.unescape()?.takeIf { it.isNotBlank() }
  val lineCount = codeText.lines().size
  Log.d(TAG, "成功渲染代碼塊，行數：$lineCount")

  Spacer(modifier = Modifier.height(8.dp))
  Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceContainer,
          tonalElevation = 1.dp,
          modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      if (lang != null) {
        Text(
                text = lang,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
        )
        HorizontalDivider(color = textColor.copy(alpha = 0.1f))
      }
      Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Text(
                text = codeText,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = textColor,
                modifier = Modifier.padding(12.dp).widthIn(min = 200.dp)
        )
      }
    }
  }
  Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun RenderIndentedCodeBlock(codeBlock: IndentedCodeBlock, textColor: Color) {
  val codeText = codeBlock.contentChars.unescape()
  Spacer(modifier = Modifier.height(4.dp))
  Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceContainer,
          tonalElevation = 1.dp,
          modifier = Modifier.fillMaxWidth()
  ) {
    Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
      Text(
              text = codeText,
              style = MaterialTheme.typography.bodyMedium,
              fontFamily = FontFamily.Monospace,
              color = textColor,
              modifier = Modifier.padding(12.dp).widthIn(min = 200.dp)
      )
    }
  }
  Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun RenderBlockQuote(blockQuote: BlockQuote, textColor: Color) {
  Spacer(modifier = Modifier.height(4.dp))
  Row(modifier = Modifier.fillMaxWidth()) {
    Box(
            modifier =
                    Modifier.width(3.dp)
                            .height(40.dp)
                            .background(textColor.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
    )
    Spacer(modifier = Modifier.width(8.dp))
    Column(modifier = Modifier.weight(1f)) {
      RenderAstNode(blockQuote, textColor.copy(alpha = 0.85f))
    }
  }
  Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun RenderBulletList(bulletList: BulletList, textColor: Color) {
  Spacer(modifier = Modifier.height(4.dp))
  Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
    var child = bulletList.firstChild
    while (child != null) {
      if (child is ListItem) {
        val annotated = buildInlineAnnotatedString(child, textColor)
        Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
        ) {
          Text("•", color = textColor, style = MaterialTheme.typography.bodyLarge)
          Spacer(modifier = Modifier.width(8.dp))
          ClickableMarkdownText(
                  annotated = annotated,
                  style = MaterialTheme.typography.bodyLarge,
                  color = textColor,
                  modifier = Modifier.weight(1f)
          )
        }
      }
      child = child.next
    }
  }
  Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun RenderOrderedList(orderedList: OrderedList, textColor: Color) {
  Spacer(modifier = Modifier.height(4.dp))
  Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
    var child = orderedList.firstChild
    var idx = 1
    while (child != null) {
      if (child is ListItem) {
        val annotated = buildInlineAnnotatedString(child, textColor)
        Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
        ) {
          Text(
                  "$idx.",
                  color = textColor,
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.width(24.dp)
          )
          ClickableMarkdownText(
                  annotated = annotated,
                  style = MaterialTheme.typography.bodyLarge,
                  color = textColor,
                  modifier = Modifier.weight(1f)
          )
        }
      }
      child = child.next
      idx++
    }
  }
  Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun RenderTable(tableBlock: TableBlock, textColor: Color) {
  val headers = mutableListOf<List<String>>()
  val rows = mutableListOf<List<String>>()

  var child = tableBlock.firstChild
  while (child != null) {
    when (child) {
      is TableHead -> {
        var rowNode = child.firstChild
        while (rowNode != null) {
          if (rowNode is TableRow) {
            headers.add(extractRowCells(rowNode))
          }
          rowNode = rowNode.next
        }
      }
      is TableBody -> {
        var rowNode = child.firstChild
        while (rowNode != null) {
          if (rowNode is TableRow) {
            rows.add(extractRowCells(rowNode))
          }
          rowNode = rowNode.next
        }
      }
    }
    child = child.next
  }

  val allRows = headers + rows
  val colCount = allRows.maxOfOrNull { it.size } ?: 0
  if (colCount == 0) return

  Log.d(TAG, "成功渲染表格，行數：${allRows.size}，列數：$colCount")

  val colWidths =
          remember(allRows) {
            val widths = IntArray(colCount)
            for (row in allRows) {
              for (c in 0 until minOf(colCount, row.size)) {
                val len = row[c].length
                if (len > widths[c]) widths[c] = len
              }
            }
            widths.map { chars -> (minOf(maxOf(chars, 6), 30) * 11).dp }
          }

  Spacer(modifier = Modifier.height(8.dp))
  Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
    Column {
      allRows.forEachIndexed { rowIdx, cells ->
        val isHeader = rowIdx < headers.size
        Row {
          for (c in 0 until colCount) {
            val cellText = if (c < cells.size) cells[c] else ""
            val bgColor =
                    if (isHeader) MaterialTheme.colorScheme.surfaceContainerHigh
                    else Color.Transparent
            Box(
                    modifier =
                            Modifier.width(colWidths[c])
                                    .background(bgColor)
                                    .border(1.dp, textColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
              Text(
                      text = cellText,
                      style = MaterialTheme.typography.bodySmall,
                      fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                      color = textColor
              )
            }
          }
        }
      }
    }
  }
  Spacer(modifier = Modifier.height(8.dp))
}

private fun extractRowCells(row: TableRow): List<String> {
  val cells = mutableListOf<String>()
  var cell = row.firstChild
  while (cell != null) {
    if (cell is TableCell) {
      cells.add(collectTextContent(cell))
    }
    cell = cell.next
  }
  return cells
}

private fun collectTextContent(node: Node): String {
  val sb = StringBuilder()
  var child = node.firstChild
  while (child != null) {
    when (child) {
      is MdText -> sb.append(child.chars.unescape())
      is Code -> sb.append(child.text.unescape())
      is SoftLineBreak -> sb.append("\n")
      is HardLineBreak -> sb.append("\n")
      else -> sb.append(collectTextContent(child))
    }
    child = child.next
  }
  return sb.toString()
}

@Composable
private fun buildInlineAnnotatedString(node: Node, textColor: Color) = buildAnnotatedString {
  appendInlineNodes(node, textColor)
}

@Composable
private fun ClickableMarkdownText(
        annotated: AnnotatedString,
        style: TextStyle,
        color: Color,
        modifier: Modifier = Modifier,
        fontWeight: FontWeight? = null,
        maxLines: Int = Int.MAX_VALUE,
        overflow: TextOverflow = TextOverflow.Clip
) {
  val uriHandler = LocalUriHandler.current
  val textStyle =
          if (fontWeight != null) style.copy(color = color, fontWeight = fontWeight)
          else style.copy(color = color)
  ClickableText(
          text = annotated,
          style = textStyle,
          modifier = modifier,
          maxLines = maxLines,
          overflow = overflow,
          onClick = { offset ->
            annotated.getStringAnnotations("URL", offset, offset).firstOrNull()?.let {
              try {
                uriHandler.openUri(it.item)
              } catch (e: Exception) {
                Log.e(TAG, "打開鏈接失敗: ${it.item}")
              }
            }
          }
  )
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.appendInlineNodes(
        node: Node,
        textColor: Color
) {
  val underlining = booleanArrayOf(false)
  appendInlineNodesInternal(node, textColor, underlining)
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.appendInlineNodesInternal(
        node: Node,
        textColor: Color,
        underlining: BooleanArray
) {
  var child = node.firstChild
  while (child != null) {
    when (child) {
      is MdText -> {
        val txt = child.chars.unescape()
        if (txt.isNotBlank() || txt.isNotEmpty()) {
          if (underlining[0]) {
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append(txt) }
          } else {
            append(txt)
          }
        }
      }
      is StrongEmphasis -> {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
          appendInlineNodesInternal(child, textColor, underlining)
        }
      }
      is Emphasis -> {
        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
          appendInlineNodesInternal(child, textColor, underlining)
        }
      }
      is Strikethrough -> {
        withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
          appendInlineNodesInternal(child, textColor, underlining)
        }
      }
      is HtmlInline -> {
        val literal = child.chars.unescape()
        if (literal == "<u>" || literal == "<ins>") {
          underlining[0] = true
        } else if (literal == "</u>" || literal == "</ins>") {
          underlining[0] = false
        }
      }
      is Code -> {
        val code = child.text.unescape()
        withStyle(
                SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = textColor.copy(alpha = 0.1f)
                )
        ) {
          if (code.isNotBlank()) {
            append(code)
          }
        }
      }
      is Link -> {
        val url = child.url.unescape()
        pushStringAnnotation(tag = "URL", annotation = url)
        withStyle(SpanStyle(color = Color(0xFF1A73E8), textDecoration = TextDecoration.Underline)) {
          appendInlineNodesInternal(child, textColor, underlining)
        }
        pop()
      }
      is SoftLineBreak -> append("\n")
      is HardLineBreak -> append("\n")
      else -> appendInlineNodesInternal(child, textColor, underlining)
    }
    child = child.next
  }
}
