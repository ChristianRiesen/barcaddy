package com.christianriesen.barcaddy.util

import com.christianriesen.barcaddy.data.Card
import com.christianriesen.barcaddy.data.CodeKind
import com.christianriesen.barcaddy.ui.components.BarcaddyFormat
import com.christianriesen.barcaddy.ui.theme.Palettes
import java.io.BufferedReader
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

/**
 * Minimal RFC 4180 CSV reader/writer scoped to Barcaddy's columns.
 * Columns: name,value,kind,format,description,palette,monogram
 */
object CsvIO {
    private val HEADER = listOf("name", "value", "kind", "format", "description", "palette", "monogram")

    fun export(cards: List<Card>, out: OutputStream) {
        out.bufferedWriter(Charsets.UTF_8).use { w ->
            w.write(HEADER.joinToString(",") { encode(it) })
            w.write("\n")
            cards.forEach { c ->
                val row = listOf(
                    c.name, c.value, c.kind.name, c.format,
                    c.description, c.palette, c.monogram,
                )
                w.write(row.joinToString(",") { encode(it) })
                w.write("\n")
            }
        }
    }

    /** Returns the parsed cards (ignores any header on a best-effort basis). */
    fun import(input: InputStream): List<Card> {
        val rows = parse(input.bufferedReader(Charsets.UTF_8))
        if (rows.isEmpty()) return emptyList()
        val first = rows[0]
        val data = if (first.size >= 2 && first[0].equals("name", true) && first[1].equals("value", true)) {
            rows.drop(1)
        } else rows
        return data.mapIndexedNotNull { idx, row -> rowToCard(row, idx) }
    }

    private fun rowToCard(row: List<String>, position: Int): Card? {
        if (row.size < 2) return null
        val name = row.getOrNull(0)?.trim().orEmpty()
        val value = row.getOrNull(1)?.trim().orEmpty()
        if (name.isEmpty() || value.isEmpty()) return null
        val kindRaw = row.getOrNull(2)?.trim().orEmpty()
        val kind = runCatching { CodeKind.valueOf(kindRaw.uppercase()) }
            .getOrElse { CodeKind.BARCODE }
        val format = row.getOrNull(3)?.trim()?.takeIf { it.isNotEmpty() }
            ?.let { BarcaddyFormat.forName(it) }
            ?: if (kind == CodeKind.QR) BarcaddyFormat.QR_CODE else BarcaddyFormat.CODE_128
        val description = row.getOrNull(4)?.trim().orEmpty()
        val paletteIn = row.getOrNull(5)?.trim().orEmpty()
        val palette = if (paletteIn.isNotBlank()) paletteIn else Palettes.random().name
        val monogram = row.getOrNull(6)?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: deriveMonogram(name)
        return Card(
            id = "c_" + UUID.randomUUID().toString().take(10),
            name = name,
            value = value,
            kind = format.kind,
            format = format.name,
            description = description,
            palette = palette,
            monogram = monogram,
            position = position,
        )
    }

    private fun deriveMonogram(name: String): String {
        val pieces = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (pieces.isEmpty()) return "NC"
        return pieces.take(2).joinToString("") { it.first().toString() }.uppercase()
    }

    private fun encode(field: String): String {
        val needsQuote = field.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        return if (needsQuote) "\"" + field.replace("\"", "\"\"") + "\"" else field
    }

    private fun parse(reader: BufferedReader): List<List<String>> {
        val text = reader.readText()
        val rows = mutableListOf<List<String>>()
        val field = StringBuilder()
        val current = mutableListOf<String>()
        var i = 0
        var inQuotes = false
        fun pushField() { current.add(field.toString()); field.clear() }
        fun pushRow() {
            pushField()
            if (current.any { it.isNotEmpty() }) rows.add(current.toList())
            current.clear()
        }
        while (i < text.length) {
            val ch = text[i]
            when {
                inQuotes && ch == '"' && i + 1 < text.length && text[i + 1] == '"' -> {
                    field.append('"'); i++
                }
                ch == '"' -> inQuotes = !inQuotes
                !inQuotes && ch == ',' -> pushField()
                !inQuotes && (ch == '\n' || ch == '\r') -> {
                    if (ch == '\r' && i + 1 < text.length && text[i + 1] == '\n') i++
                    pushRow()
                }
                else -> field.append(ch)
            }
            i++
        }
        if (field.isNotEmpty() || current.isNotEmpty()) pushRow()
        return rows
    }
}
