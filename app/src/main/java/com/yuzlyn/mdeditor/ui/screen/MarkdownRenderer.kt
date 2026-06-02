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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableBody
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.ext.gfm.tables.TableHead
import org.commonmark.ext.gfm.tables.TableRow
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text as MdText
import org.commonmark.node.ThematicBreak
import org.commonmark.parser.Parser

private const val TAG = "MDEditor_Debug"

private val markdownParser: Parser by lazy {
  Parser.builder().extensions(listOf(TablesExtension.create())).build()
}

@Composable
fun MarkdownPreview(
        markdown: String,
        textColor: Color,
        scrollState: androidx.compose.foundation.ScrollState = rememberScrollState(),
        modifier: Modifier = Modifier
) {
  Log.d(TAG, "Markdown 预览解析启动")

  val rootNode =
          remember(markdown) {
            try {
              markdownParser.parse(markdown)
            } catch (e: Exception) {
              Log.e(TAG, "Markdown 解析失败: ${e.message}")
              null
            }
          }

  Column(
          modifier = modifier.verticalScroll(scrollState).fillMaxWidth().padding(horizontal = 16.dp)
  ) {
    if (rootNode == null || markdown.isBlank()) {
      Text(
              "预览区域为空",
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
  Text(text = annotated, style = style, fontWeight = FontWeight.Bold, color = textColor)
  Spacer(modifier = Modifier.height(if (heading.level <= 2) 8.dp else 4.dp))
}

@Composable
private fun RenderParagraph(paragraph: Paragraph, textColor: Color) {
  val annotated = buildInlineAnnotatedString(paragraph, textColor)
  Spacer(modifier = Modifier.height(8.dp))
  Text(text = annotated, style = MaterialTheme.typography.bodyLarge, color = textColor)
  Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun RenderFencedCodeBlock(codeBlock: FencedCodeBlock, textColor: Color) {
  val codeText = codeBlock.literal.trimEnd()
  val lang = codeBlock.info?.takeIf { it.isNotBlank() }
  val lineCount = codeText.lines().size
  Log.d(TAG, "成功渲染代码块，行数：$lineCount")

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
  val codeText = codeBlock.literal.trimEnd()
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
    var idx = 0
    while (child != null) {
      if (child is ListItem) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
          Text("•", color = textColor, style = MaterialTheme.typography.bodyLarge)
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f)) { RenderAstNode(child, textColor) }
        }
      }
      child = child.next
      idx++
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
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
          Text(
                  "$idx.",
                  color = textColor,
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.width(24.dp)
          )
          Column(modifier = Modifier.weight(1f)) { RenderAstNode(child, textColor) }
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
  var alignment = listOf<TableCell.Alignment>()

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

  Log.d(TAG, "成功渲染表格，行数：${allRows.size}，列数：$colCount")

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
      is MdText -> sb.append(child.literal)
      is Code -> sb.append(child.literal)
      is SoftLineBreak -> sb.append(" ")
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

private fun androidx.compose.ui.text.AnnotatedString.Builder.appendInlineNodes(
        node: Node,
        textColor: Color
) {
  var child = node.firstChild
  while (child != null) {
    when (child) {
      is MdText -> {
        val txt = child.literal
        if (txt.isNotBlank() || txt.isNotEmpty()) {
          append(txt)
        }
      }
      is StrongEmphasis -> {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { appendInlineNodes(child, textColor) }
      }
      is Emphasis -> {
        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { appendInlineNodes(child, textColor) }
      }
      is Code -> {
        val code = child.literal
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
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
          appendInlineNodes(child, textColor)
        }
      }
      is SoftLineBreak -> append(" ")
      is HardLineBreak -> append("\n")
      else -> appendInlineNodes(child, textColor)
    }
    child = child.next
  }
}
