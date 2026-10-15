package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LegalCode
import com.example.ui.theme.*

/**
 * Editorial Cover Design Tokens for all 19 Uzbekistan Legal Codes.
 * Each code features an intentional color palette, legal motif, and architectural geometry.
 */
data class CoverStyle(
    val primaryBg: Color,
    val secondaryBg: Color,
    val accentColor: Color,
    val goldAccent: Color = Color(0xFFD4AF37),
    val motifType: String,
    val categoryLabel: String
)

object CodeCoverStyles {
    fun getStyleForCode(codeId: String): CoverStyle {
        return when (codeId.lowercase()) {
            "fk" -> CoverStyle(
                primaryBg = Color(0xFF0F3A40),
                secondaryBg = Color(0xFF071E22),
                accentColor = Color(0xFF26A69A),
                goldAccent = Color(0xFFE2B755),
                motifType = "COLUMNS",
                categoryLabel = "Xususiy huquq"
            )
            "fpk" -> CoverStyle(
                primaryBg = Color(0xFF0F2B48),
                secondaryBg = Color(0xFF081729),
                accentColor = Color(0xFF38BDF8),
                goldAccent = Color(0xFFE5C07B),
                motifType = "ARCH",
                categoryLabel = "Protsessual huquq"
            )
            "jk" -> CoverStyle(
                primaryBg = Color(0xFF221526),
                secondaryBg = Color(0xFF120A14),
                accentColor = Color(0xFFE53935),
                goldAccent = Color(0xFFE5B94E),
                motifType = "SCALES",
                categoryLabel = "Jinoiy huquq"
            )
            "jpk" -> CoverStyle(
                primaryBg = Color(0xFF16253B),
                secondaryBg = Color(0xFF0B1421),
                accentColor = Color(0xFF60A5FA),
                goldAccent = Color(0xFFE2B755),
                motifType = "GAVEL",
                categoryLabel = "Protsessual huquq"
            )
            "jik" -> CoverStyle(
                primaryBg = Color(0xFF242B35),
                secondaryBg = Color(0xFF11161D),
                accentColor = Color(0xFF94A3B8),
                goldAccent = Color(0xFFD4AF37),
                motifType = "GATE",
                categoryLabel = "Jinoiy huquq"
            )
            "ipk" -> CoverStyle(
                primaryBg = Color(0xFF0D332B),
                secondaryBg = Color(0xFF061C17),
                accentColor = Color(0xFF34D399),
                goldAccent = Color(0xFFF1C40F),
                motifType = "GROWTH",
                categoryLabel = "Iqtisodiy sud"
            )
            "msiyk" -> CoverStyle(
                primaryBg = Color(0xFF221F42),
                secondaryBg = Color(0xFF110F24),
                accentColor = Color(0xFF818CF8),
                goldAccent = Color(0xFFE5B94E),
                motifType = "ROTUNDA",
                categoryLabel = "Ma'muriy sud"
            )
            "mjtk" -> CoverStyle(
                primaryBg = Color(0xFF332214),
                secondaryBg = Color(0xFF1C1108),
                accentColor = Color(0xFFF59E0B),
                goldAccent = Color(0xFFFCD34D),
                motifType = "SHIELD",
                categoryLabel = "Ommaviy huquq"
            )
            "mk" -> CoverStyle(
                primaryBg = Color(0xFF0D3E39),
                secondaryBg = Color(0xFF062320),
                accentColor = Color(0xFF2DD4BF),
                goldAccent = Color(0xFFE5C07B),
                motifType = "SUNRISE",
                categoryLabel = "Mehnat huquqi"
            )
            "ok" -> CoverStyle(
                primaryBg = Color(0xFF3B1A28),
                secondaryBg = Color(0xFF200C15),
                accentColor = Color(0xFFF472B6),
                goldAccent = Color(0xFFFDE047),
                motifType = "RINGS",
                categoryLabel = "Oila huquqi"
            )
            "sk" -> CoverStyle(
                primaryBg = Color(0xFF143428),
                secondaryBg = Color(0xFF0A1F17),
                accentColor = Color(0xFF4ADE80),
                goldAccent = Color(0xFFFACC15),
                motifType = "LEDGER",
                categoryLabel = "Soliq tizimi"
            )
            "bk" -> CoverStyle(
                primaryBg = Color(0xFF102844),
                secondaryBg = Color(0xFF081628),
                accentColor = Color(0xFF38BDF8),
                goldAccent = Color(0xFFE5C07B),
                motifType = "COMPASS",
                categoryLabel = "Bojxona tizimi"
            )
            "byk" -> CoverStyle(
                primaryBg = Color(0xFF2E2413),
                secondaryBg = Color(0xFF191307),
                accentColor = Color(0xFFFBBF24),
                goldAccent = Color(0xFFFDE68A),
                motifType = "VAULT",
                categoryLabel = "Moliya huquqi"
            )
            "yk" -> CoverStyle(
                primaryBg = Color(0xFF1B331D),
                secondaryBg = Color(0xFF0C1B0D),
                accentColor = Color(0xFFA3E635),
                goldAccent = Color(0xFFFACC15),
                motifType = "LAND",
                categoryLabel = "Yer resurslari"
            )
            "ujk" -> CoverStyle(
                primaryBg = Color(0xFF342016),
                secondaryBg = Color(0xFF1C100A),
                accentColor = Color(0xFFFB923C),
                goldAccent = Color(0xFFFDE047),
                motifType = "HOUSE",
                categoryLabel = "Uy-joy huquqi"
            )
            "shk" -> CoverStyle(
                primaryBg = Color(0xFF122C3E),
                secondaryBg = Color(0xFF091722),
                accentColor = Color(0xFF22D3EE),
                goldAccent = Color(0xFFE5C07B),
                motifType = "SKYLINE",
                categoryLabel = "Shaharsozlik"
            )
            "hk" -> CoverStyle(
                primaryBg = Color(0xFF0F2C4C),
                secondaryBg = Color(0xFF07182C),
                accentColor = Color(0xFF67E8F9),
                goldAccent = Color(0xFFE2B755),
                motifType = "WINGS",
                categoryLabel = "Aviatsiya huquqi"
            )
            "sayk" -> CoverStyle(
                primaryBg = Color(0xFF132A52),
                secondaryBg = Color(0xFF09162D),
                accentColor = Color(0xFF38BDF8),
                goldAccent = Color(0xFFFACC15),
                motifType = "BALLOT",
                categoryLabel = "Saylov tizimi"
            )
            "suk" -> CoverStyle(
                primaryBg = Color(0xFF0A333B),
                secondaryBg = Color(0xFF04191D),
                accentColor = Color(0xFF2DD4BF),
                goldAccent = Color(0xFF5EEAD4),
                motifType = "WATER",
                categoryLabel = "Suv resurslari"
            )
            else -> CoverStyle(
                primaryBg = Color(0xFF0F3A40),
                secondaryBg = Color(0xFF071E22),
                accentColor = Color(0xFF26A69A),
                goldAccent = Color(0xFFE2B755),
                motifType = "COLUMNS",
                categoryLabel = "Qonunchilik"
            )
        }
    }
}

/**
 * Editorial Large Book Cover Illustration for Uzbekistan Legal Codes.
 * Rendered with authentic Uzbek architectural motifs, classical legal symbolism,
 * 8-pointed gold stars, layered gradients, and gold-foiled framing.
 */
@Composable
fun CodeCoverIllustration(
    code: LegalCode,
    modifier: Modifier = Modifier,
    height: Dp = 210.dp,
    showDetailedHeader: Boolean = true
) {
    val style = CodeCoverStyles.getStyleForCode(code.id)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = style.secondaryBg),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, style.goldAccent.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .testTag("code_cover_${code.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Canvas: Layered gradient & architectural geometric illustration
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Base subtle gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(style.primaryBg, style.secondaryBg)
                    )
                )

                // Elegant Gold Foil Border Inside Cover
                val margin = 8.dp.toPx()
                drawRoundRect(
                    color = style.goldAccent.copy(alpha = 0.35f),
                    topLeft = Offset(margin, margin),
                    size = Size(w - 2 * margin, h - 2 * margin),
                    cornerRadius = CornerRadius(10.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )

                // Inner fine decorative frame
                val innerMargin = 12.dp.toPx()
                drawRoundRect(
                    color = style.goldAccent.copy(alpha = 0.16f),
                    topLeft = Offset(innerMargin, innerMargin),
                    size = Size(w - 2 * innerMargin, h - 2 * innerMargin),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    style = Stroke(width = 0.6.dp.toPx())
                )

                // Draw Uzbek 8-pointed stars in the four corners
                drawUzbekCornerStars(style.goldAccent, w, h, innerMargin)

                // Draw central bespoke editorial illustration according to code motif
                drawEditorialMotif(
                    motifType = style.motifType,
                    accentColor = style.accentColor,
                    goldColor = style.goldAccent,
                    w = w,
                    h = h
                )
            }

            // Foreground Editorial Typography Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header: Republic of Uzbekistan
                if (showDetailedHeader) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "O'ZBEKISTON RESPUBLIKASI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                letterSpacing = 1.4.sp,
                                fontWeight = FontWeight.Bold,
                                color = style.goldAccent
                            ),
                            textAlign = TextAlign.Center
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .width(36.dp)
                                .height(1.dp)
                                .background(style.goldAccent.copy(alpha = 0.6f))
                        )
                        Text(
                            text = style.categoryLabel.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 7.5.sp,
                                letterSpacing = 1.sp,
                                color = style.accentColor
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Title Plate (Embossed look)
                Surface(
                    color = Color.Black.copy(alpha = 0.58f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, style.goldAccent.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = code.shortName.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.6.sp
                            ),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = code.documentNumber,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = style.goldAccent
                                )
                            )
                            Text(
                                text = " • ${code.totalArticlesEstimate} modda",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact Code Card with mini illustrated cover thumbnail for grid or lists.
 */
@Composable
fun CodeCoverCard(
    code: LegalCode,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val style = CodeCoverStyles.getStyleForCode(code.id)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, UzLegalBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("code_card_${code.id}")
    ) {
        Column {
            // Illustrated top banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(style.primaryBg, style.secondaryBg)
                        )
                    )

                    // Inner gold border
                    val pad = 6.dp.toPx()
                    drawRoundRect(
                        color = style.goldAccent.copy(alpha = 0.35f),
                        topLeft = Offset(pad, pad),
                        size = Size(w - 2 * pad, h - 2 * pad),
                        cornerRadius = CornerRadius(6.dp.toPx()),
                        style = Stroke(width = 0.8.dp.toPx())
                    )

                    drawEditorialMotif(
                        motifType = style.motifType,
                        accentColor = style.accentColor,
                        goldColor = style.goldAccent,
                        w = w,
                        h = h
                    )
                }

                // Top badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Number badge
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(0.5.dp, style.goldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = style.goldAccent
                            )
                        )
                    }

                    // Status pill
                    Surface(
                        color = Color.Black.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, style.goldAccent.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = code.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Bottom title & info
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = code.shortName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${code.totalArticlesEstimate} modda",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = UzLegalSlateBody,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = code.documentNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = UzLegalTeal,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Draws 4 delicate traditional 8-pointed Uzbek stars (Rub el Hizb) at inner corners
 */
private fun DrawScope.drawUzbekCornerStars(color: Color, w: Float, h: Float, margin: Float) {
    val size = 5.dp.toPx()
    val offsets = listOf(
        Offset(margin + 8.dp.toPx(), margin + 8.dp.toPx()),
        Offset(w - margin - 8.dp.toPx(), margin + 8.dp.toPx()),
        Offset(margin + 8.dp.toPx(), h - margin - 8.dp.toPx()),
        Offset(w - margin - 8.dp.toPx(), h - margin - 8.dp.toPx())
    )

    offsets.forEach { center ->
        // Two intersecting squares rotated 45 degrees
        drawRect(
            color = color.copy(alpha = 0.35f),
            topLeft = Offset(center.x - size / 2, center.y - size / 2),
            size = Size(size, size),
            style = Stroke(width = 0.8.dp.toPx())
        )
        // Diamond
        val diaPath = Path().apply {
            moveTo(center.x, center.y - size * 0.7f)
            lineTo(center.x + size * 0.7f, center.y)
            lineTo(center.x, center.y + size * 0.7f)
            lineTo(center.x - size * 0.7f, center.y)
            close()
        }
        drawPath(diaPath, color = color.copy(alpha = 0.35f), style = Stroke(width = 0.8.dp.toPx()))
    }
}

/**
 * Draws bespoke architectural and legal vector motifs for each code
 */
private fun DrawScope.drawEditorialMotif(
    motifType: String,
    accentColor: Color,
    goldColor: Color,
    w: Float,
    h: Float
) {
    val cx = w / 2f
    val cy = h * 0.44f

    when (motifType) {
        "COLUMNS" -> {
            // Fuqarolik kodeksi: Classical Civic Pediment & Fluted Columns
            val colTop = cy - 16.dp.toPx()
            val colBottom = cy + 24.dp.toPx()
            val pedimentTop = cy - 34.dp.toPx()

            val pedPath = Path().apply {
                moveTo(cx - 34.dp.toPx(), colTop)
                lineTo(cx, pedimentTop)
                lineTo(cx + 34.dp.toPx(), colTop)
                close()
            }
            drawPath(pedPath, color = accentColor.copy(alpha = 0.22f))
            drawPath(pedPath, color = goldColor, style = Stroke(width = 1.5.dp.toPx()))

            // 4 Pillars
            listOf(-24.dp.toPx(), -8.dp.toPx(), 8.dp.toPx(), 24.dp.toPx()).forEach { xOff ->
                drawLine(goldColor, Offset(cx + xOff, colTop), Offset(cx + xOff, colBottom), strokeWidth = 2.dp.toPx())
            }
            // Base podium
            drawLine(goldColor, Offset(cx - 38.dp.toPx(), colBottom), Offset(cx + 38.dp.toPx(), colBottom), strokeWidth = 3.dp.toPx())
            // Emblem inside pediment
            drawCircle(goldColor, radius = 3.dp.toPx(), center = Offset(cx, cy - 22.dp.toPx()))
        }

        "SCALES" -> {
            // Jinoyat kodeksi: Dignified Scales of Justice & Sword of Retribution
            val beamY = cy - 18.dp.toPx()
            val beamHalf = 30.dp.toPx()

            // Vertical blade / central pillar
            drawLine(goldColor, Offset(cx, cy - 32.dp.toPx()), Offset(cx, cy + 26.dp.toPx()), strokeWidth = 2.dp.toPx())
            // Base stand
            drawLine(goldColor, Offset(cx - 18.dp.toPx(), cy + 26.dp.toPx()), Offset(cx + 18.dp.toPx(), cy + 26.dp.toPx()), strokeWidth = 2.5.dp.toPx())
            // Fulcrum balance beam
            drawLine(goldColor, Offset(cx - beamHalf, beamY), Offset(cx + beamHalf, beamY), strokeWidth = 2.dp.toPx())

            // Left scale pan
            val lPanY = beamY + 22.dp.toPx()
            drawLine(accentColor, Offset(cx - beamHalf, beamY), Offset(cx - beamHalf - 10.dp.toPx(), lPanY), strokeWidth = 1.dp.toPx())
            drawLine(accentColor, Offset(cx - beamHalf, beamY), Offset(cx - beamHalf + 10.dp.toPx(), lPanY), strokeWidth = 1.dp.toPx())
            drawLine(goldColor, Offset(cx - beamHalf - 12.dp.toPx(), lPanY), Offset(cx - beamHalf + 12.dp.toPx(), lPanY), strokeWidth = 2.dp.toPx())

            // Right scale pan
            val rPanY = beamY + 22.dp.toPx()
            drawLine(accentColor, Offset(cx + beamHalf, beamY), Offset(cx + beamHalf - 10.dp.toPx(), rPanY), strokeWidth = 1.dp.toPx())
            drawLine(accentColor, Offset(cx + beamHalf, beamY), Offset(cx + beamHalf + 10.dp.toPx(), rPanY), strokeWidth = 1.dp.toPx())
            drawLine(goldColor, Offset(cx + beamHalf - 12.dp.toPx(), rPanY), Offset(cx + beamHalf + 12.dp.toPx(), rPanY), strokeWidth = 2.dp.toPx())
        }

        "GAVEL" -> {
            // Jinoyat-protsessual kodeksi: Judicial Gavel & Sound Block with concentric rings
            // Sound block
            drawRoundRect(
                color = goldColor.copy(alpha = 0.3f),
                topLeft = Offset(cx - 24.dp.toPx(), cy + 12.dp.toPx()),
                size = Size(48.dp.toPx(), 12.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Acoustic ripple
            drawArc(
                color = accentColor.copy(alpha = 0.45f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - 32.dp.toPx(), cy + 6.dp.toPx()),
                size = Size(64.dp.toPx(), 24.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
            // Gavel Head (angled 30 degrees)
            val headPath = Path().apply {
                moveTo(cx - 8.dp.toPx(), cy - 24.dp.toPx())
                lineTo(cx + 18.dp.toPx(), cy - 8.dp.toPx())
                lineTo(cx + 12.dp.toPx(), cy)
                lineTo(cx - 14.dp.toPx(), cy - 16.dp.toPx())
                close()
            }
            drawPath(headPath, color = accentColor.copy(alpha = 0.4f))
            drawPath(headPath, color = goldColor, style = Stroke(width = 1.5.dp.toPx()))

            // Handle
            drawLine(goldColor, Offset(cx + 4.dp.toPx(), cy - 6.dp.toPx()), Offset(cx + 26.dp.toPx(), cy + 16.dp.toPx()), strokeWidth = 2.5.dp.toPx())
        }

        "ARCH" -> {
            // Fuqarolik protsessual: Majestic Tri-Fold Judicial Archway
            val outerW = 48.dp.toPx()
            val outerH = 54.dp.toPx()
            drawRoundRect(
                color = accentColor.copy(alpha = 0.18f),
                topLeft = Offset(cx - outerW / 2, cy - outerH / 2),
                size = Size(outerW, outerH),
                cornerRadius = CornerRadius(outerW / 2, outerW / 2)
            )
            drawRoundRect(
                color = goldColor,
                topLeft = Offset(cx - outerW / 2, cy - outerH / 2),
                size = Size(outerW, outerH),
                cornerRadius = CornerRadius(outerW / 2, outerW / 2),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Inner portal arch
            val innerW = 28.dp.toPx()
            val innerH = 38.dp.toPx()
            drawRoundRect(
                color = goldColor.copy(alpha = 0.5f),
                topLeft = Offset(cx - innerW / 2, cy - innerH / 2 + 8.dp.toPx()),
                size = Size(innerW, innerH),
                cornerRadius = CornerRadius(innerW / 2, innerW / 2),
                style = Stroke(width = 1.dp.toPx())
            )
            // Center justice star
            drawCircle(goldColor, radius = 3.5.dp.toPx(), center = Offset(cx, cy - 8.dp.toPx()))
        }

        "SUNRISE" -> {
            // Mehnat kodeksi: Radiant Sunrise & Overlapping Work Cogwheel
            val r = 24.dp.toPx()
            drawCircle(color = accentColor.copy(alpha = 0.2f), radius = r, center = Offset(cx, cy))
            drawCircle(color = goldColor, radius = r, center = Offset(cx, cy), style = Stroke(width = 1.5.dp.toPx()))

            // 12 Sun rays
            for (i in 0 until 12) {
                val angle = Math.toRadians((i * 30).toDouble())
                val x1 = cx + (r + 3.dp.toPx()) * Math.cos(angle).toFloat()
                val y1 = cy + (r + 3.dp.toPx()) * Math.sin(angle).toFloat()
                val x2 = cx + (r + 9.dp.toPx()) * Math.cos(angle).toFloat()
                val y2 = cy + (r + 9.dp.toPx()) * Math.sin(angle).toFloat()
                drawLine(goldColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 1.5.dp.toPx())
            }
            // Inner work handshake geometry
            drawLine(goldColor, Offset(cx - 12.dp.toPx(), cy + 2.dp.toPx()), Offset(cx, cy - 6.dp.toPx()), strokeWidth = 2.dp.toPx())
            drawLine(goldColor, Offset(cx, cy - 6.dp.toPx()), Offset(cx + 12.dp.toPx(), cy + 2.dp.toPx()), strokeWidth = 2.dp.toPx())
            drawLine(goldColor, Offset(cx - 8.dp.toPx(), cy + 6.dp.toPx()), Offset(cx + 8.dp.toPx(), cy + 6.dp.toPx()), strokeWidth = 2.dp.toPx())
        }

        "RINGS" -> {
            // Oila kodeksi: Interlocking Protective Rings & Warm Hearth Shield
            val r = 16.dp.toPx()
            val yPos = cy - 2.dp.toPx()
            // Left ring
            drawCircle(color = accentColor.copy(alpha = 0.25f), radius = r, center = Offset(cx - 10.dp.toPx(), yPos))
            drawCircle(color = goldColor, radius = r, center = Offset(cx - 10.dp.toPx(), yPos), style = Stroke(width = 2.dp.toPx()))
            // Right ring
            drawCircle(color = accentColor.copy(alpha = 0.25f), radius = r, center = Offset(cx + 10.dp.toPx(), yPos))
            drawCircle(color = goldColor, radius = r, center = Offset(cx + 10.dp.toPx(), yPos), style = Stroke(width = 2.dp.toPx()))
            // Upper protective apex crown
            val crownPath = Path().apply {
                moveTo(cx - 16.dp.toPx(), cy - 22.dp.toPx())
                lineTo(cx, cy - 30.dp.toPx())
                lineTo(cx + 16.dp.toPx(), cy - 22.dp.toPx())
            }
            drawPath(crownPath, color = goldColor, style = Stroke(width = 1.5.dp.toPx()))
        }

        "LEDGER" -> {
            // Soliq kodeksi: Structured Fiscal Ledger & Sovereign Treasury Balance
            val bookW = 44.dp.toPx()
            val bookH = 48.dp.toPx()
            // Open ledger
            drawRoundRect(
                color = accentColor.copy(alpha = 0.25f),
                topLeft = Offset(cx - bookW / 2, cy - bookH / 2),
                size = Size(bookW, bookH),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
            drawRoundRect(
                color = goldColor,
                topLeft = Offset(cx - bookW / 2, cy - bookH / 2),
                size = Size(bookW, bookH),
                cornerRadius = CornerRadius(4.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Center spine
            drawLine(goldColor, Offset(cx, cy - bookH / 2), Offset(cx, cy + bookH / 2), strokeWidth = 1.5.dp.toPx())
            // Ruled entry lines
            listOf(-12.dp.toPx(), -4.dp.toPx(), 4.dp.toPx(), 12.dp.toPx()).forEach { yOff ->
                drawLine(goldColor.copy(alpha = 0.6f), Offset(cx - 16.dp.toPx(), cy + yOff), Offset(cx - 4.dp.toPx(), cy + yOff), strokeWidth = 1.dp.toPx())
                drawLine(goldColor.copy(alpha = 0.6f), Offset(cx + 4.dp.toPx(), cy + yOff), Offset(cx + 16.dp.toPx(), cy + yOff), strokeWidth = 1.dp.toPx())
            }
        }

        "COMPASS" -> {
            // Bojxona kodeksi: 8-Point Navigation Compass Rose & Border Gate
            val r = 24.dp.toPx()
            drawCircle(color = accentColor.copy(alpha = 0.2f), radius = r, center = Offset(cx, cy))
            drawCircle(color = goldColor, radius = r, center = Offset(cx, cy), style = Stroke(width = 1.5.dp.toPx()))

            // 4 Primary Points (Diamond star)
            val starPath = Path().apply {
                moveTo(cx, cy - 28.dp.toPx())
                lineTo(cx + 6.dp.toPx(), cy - 6.dp.toPx())
                lineTo(cx + 28.dp.toPx(), cy)
                lineTo(cx + 6.dp.toPx(), cy + 6.dp.toPx())
                lineTo(cx, cy + 28.dp.toPx())
                lineTo(cx - 6.dp.toPx(), cy + 6.dp.toPx())
                lineTo(cx - 28.dp.toPx(), cy)
                lineTo(cx - 6.dp.toPx(), cy - 6.dp.toPx())
                close()
            }
            drawPath(starPath, color = goldColor, style = Stroke(width = 1.5.dp.toPx()))
            drawCircle(goldColor, radius = 3.dp.toPx(), center = Offset(cx, cy))
        }

        "VAULT" -> {
            // Byudjet kodeksi: Heavy Circular Treasury Vault Door
            val r = 24.dp.toPx()
            drawCircle(color = accentColor.copy(alpha = 0.25f), radius = r, center = Offset(cx, cy))
            drawCircle(color = goldColor, radius = r, center = Offset(cx, cy), style = Stroke(width = 2.dp.toPx()))
            drawCircle(color = goldColor, radius = r * 0.5f, center = Offset(cx, cy), style = Stroke(width = 1.5.dp.toPx()))

            // 6 Locking bolt spokes
            for (i in 0 until 6) {
                val angle = Math.toRadians((i * 60).toDouble())
                val x1 = cx + (r * 0.5f) * Math.cos(angle).toFloat()
                val y1 = cy + (r * 0.5f) * Math.sin(angle).toFloat()
                val x2 = cx + (r + 4.dp.toPx()) * Math.cos(angle).toFloat()
                val y2 = cy + (r + 4.dp.toPx()) * Math.sin(angle).toFloat()
                drawLine(goldColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 2.dp.toPx())
            }
            drawCircle(goldColor, radius = 4.dp.toPx(), center = Offset(cx, cy))
        }

        "LAND" -> {
            // Yer kodeksi: Contoured Agricultural Furrows, Sun & Land Horizon
            // Sun on horizon
            drawCircle(color = goldColor.copy(alpha = 0.5f), radius = 14.dp.toPx(), center = Offset(cx, cy - 10.dp.toPx()))

            // Horizon line
            drawLine(goldColor, Offset(cx - 32.dp.toPx(), cy - 2.dp.toPx()), Offset(cx + 32.dp.toPx(), cy - 2.dp.toPx()), strokeWidth = 1.5.dp.toPx())

            // Terraced furrow curves
            val p1 = Path().apply {
                moveTo(cx - 30.dp.toPx(), cy + 8.dp.toPx())
                cubicTo(cx - 10.dp.toPx(), cy + 4.dp.toPx(), cx + 10.dp.toPx(), cy + 12.dp.toPx(), cx + 30.dp.toPx(), cy + 6.dp.toPx())
            }
            drawPath(p1, color = goldColor, style = Stroke(width = 1.5.dp.toPx()))

            val p2 = Path().apply {
                moveTo(cx - 32.dp.toPx(), cy + 18.dp.toPx())
                cubicTo(cx - 10.dp.toPx(), cy + 12.dp.toPx(), cx + 10.dp.toPx(), cy + 22.dp.toPx(), cx + 32.dp.toPx(), cy + 16.dp.toPx())
            }
            drawPath(p2, color = accentColor, style = Stroke(width = 2.dp.toPx()))
        }

        "HOUSE" -> {
            // Uy-joy kodeksi: Residential Gable Roofline & Hearth Arch
            val roofPath = Path().apply {
                moveTo(cx - 30.dp.toPx(), cy - 2.dp.toPx())
                lineTo(cx, cy - 28.dp.toPx())
                lineTo(cx + 30.dp.toPx(), cy - 2.dp.toPx())
            }
            drawPath(roofPath, color = goldColor, style = Stroke(width = 2.dp.toPx()))

            // House walls
            drawRect(
                color = accentColor.copy(alpha = 0.25f),
                topLeft = Offset(cx - 24.dp.toPx(), cy - 2.dp.toPx()),
                size = Size(48.dp.toPx(), 26.dp.toPx())
            )
            drawRect(
                color = goldColor,
                topLeft = Offset(cx - 24.dp.toPx(), cy - 2.dp.toPx()),
                size = Size(48.dp.toPx(), 26.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Door arch
            drawRoundRect(
                color = goldColor,
                topLeft = Offset(cx - 8.dp.toPx(), cy + 8.dp.toPx()),
                size = Size(16.dp.toPx(), 16.dp.toPx()),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        "SKYLINE" -> {
            // Shaharsozlik kodeksi: Tashkent Architecture Silhouette & Blueprint Grid
            // Blueprint coordinate grid lines
            listOf(-20.dp.toPx(), 0.dp.toPx(), 20.dp.toPx()).forEach { xOff ->
                drawLine(accentColor.copy(alpha = 0.35f), Offset(cx + xOff, cy - 26.dp.toPx()), Offset(cx + xOff, cy + 22.dp.toPx()), strokeWidth = 0.8.dp.toPx())
            }
            listOf(-16.dp.toPx(), 0.dp.toPx(), 16.dp.toPx()).forEach { yOff ->
                drawLine(accentColor.copy(alpha = 0.35f), Offset(cx - 28.dp.toPx(), cy + yOff), Offset(cx + 28.dp.toPx(), cy + yOff), strokeWidth = 0.8.dp.toPx())
            }

            // Towers
            drawRect(color = goldColor.copy(alpha = 0.4f), topLeft = Offset(cx - 24.dp.toPx(), cy - 10.dp.toPx()), size = Size(12.dp.toPx(), 32.dp.toPx()))
            drawRect(color = goldColor, topLeft = Offset(cx - 24.dp.toPx(), cy - 10.dp.toPx()), size = Size(12.dp.toPx(), 32.dp.toPx()), style = Stroke(width = 1.5.dp.toPx()))

            drawRect(color = accentColor.copy(alpha = 0.5f), topLeft = Offset(cx - 8.dp.toPx(), cy - 24.dp.toPx()), size = Size(16.dp.toPx(), 46.dp.toPx()))
            drawRect(color = goldColor, topLeft = Offset(cx - 8.dp.toPx(), cy - 24.dp.toPx()), size = Size(16.dp.toPx(), 46.dp.toPx()), style = Stroke(width = 1.5.dp.toPx()))

            drawRect(color = goldColor.copy(alpha = 0.4f), topLeft = Offset(cx + 12.dp.toPx(), cy - 16.dp.toPx()), size = Size(14.dp.toPx(), 38.dp.toPx()))
            drawRect(color = goldColor, topLeft = Offset(cx + 12.dp.toPx(), cy - 16.dp.toPx()), size = Size(14.dp.toPx(), 38.dp.toPx()), style = Stroke(width = 1.5.dp.toPx()))
        }

        "WINGS" -> {
            // Havo kodeksi: Aerodynamic Swept Wings & Stratosphere Arc
            val wingL = Path().apply {
                moveTo(cx, cy + 12.dp.toPx())
                cubicTo(cx - 10.dp.toPx(), cy, cx - 24.dp.toPx(), cy - 14.dp.toPx(), cx - 34.dp.toPx(), cy - 22.dp.toPx())
                cubicTo(cx - 24.dp.toPx(), cy - 10.dp.toPx(), cx - 12.dp.toPx(), cy + 4.dp.toPx(), cx, cy + 12.dp.toPx())
            }
            drawPath(wingL, color = accentColor.copy(alpha = 0.4f))
            drawPath(wingL, color = goldColor, style = Stroke(width = 1.5.dp.toPx()))

            val wingR = Path().apply {
                moveTo(cx, cy + 12.dp.toPx())
                cubicTo(cx + 10.dp.toPx(), cy, cx + 24.dp.toPx(), cy - 14.dp.toPx(), cx + 34.dp.toPx(), cy - 22.dp.toPx())
                cubicTo(cx + 24.dp.toPx(), cy - 10.dp.toPx(), cx + 12.dp.toPx(), cy + 4.dp.toPx(), cx, cy + 12.dp.toPx())
            }
            drawPath(wingR, color = accentColor.copy(alpha = 0.4f))
            drawPath(wingR, color = goldColor, style = Stroke(width = 1.5.dp.toPx()))

            // North altitude star
            drawCircle(goldColor, radius = 3.dp.toPx(), center = Offset(cx, cy - 18.dp.toPx()))
        }

        "BALLOT" -> {
            // Saylov kodeksi: Modern Civic Ballot Box with Voting Slip & Democratic Star
            // Box
            drawRect(
                color = accentColor.copy(alpha = 0.25f),
                topLeft = Offset(cx - 22.dp.toPx(), cy - 6.dp.toPx()),
                size = Size(44.dp.toPx(), 30.dp.toPx())
            )
            drawRect(
                color = goldColor,
                topLeft = Offset(cx - 22.dp.toPx(), cy - 6.dp.toPx()),
                size = Size(44.dp.toPx(), 30.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Ballot slot
            drawLine(goldColor, Offset(cx - 14.dp.toPx(), cy - 6.dp.toPx()), Offset(cx + 14.dp.toPx(), cy - 6.dp.toPx()), strokeWidth = 2.dp.toPx())

            // Paper slip entering slot
            val slipPath = Path().apply {
                moveTo(cx - 8.dp.toPx(), cy - 22.dp.toPx())
                lineTo(cx + 8.dp.toPx(), cy - 22.dp.toPx())
                lineTo(cx + 8.dp.toPx(), cy - 6.dp.toPx())
                lineTo(cx - 8.dp.toPx(), cy - 6.dp.toPx())
                close()
            }
            drawPath(slipPath, color = Color.White.copy(alpha = 0.7f))
            drawPath(slipPath, color = goldColor, style = Stroke(width = 1.dp.toPx()))

            // 8-point Uzbek civic star on the box
            drawCircle(goldColor, radius = 4.dp.toPx(), center = Offset(cx, cy + 9.dp.toPx()))
        }

        "WATER" -> {
            // Suv kodeksi: Aqueduct Arches & Flowing Hydrological Droplet
            val dropPath = Path().apply {
                moveTo(cx, cy - 26.dp.toPx())
                cubicTo(cx + 18.dp.toPx(), cy - 6.dp.toPx(), cx + 18.dp.toPx(), cy + 14.dp.toPx(), cx, cy + 18.dp.toPx())
                cubicTo(cx - 18.dp.toPx(), cy + 14.dp.toPx(), cx - 18.dp.toPx(), cy - 6.dp.toPx(), cx, cy - 26.dp.toPx())
                close()
            }
            drawPath(dropPath, color = accentColor.copy(alpha = 0.35f))
            drawPath(dropPath, color = goldColor, style = Stroke(width = 1.5.dp.toPx()))

            // Hydrological ripple arcs
            drawArc(
                color = goldColor.copy(alpha = 0.7f),
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(cx - 26.dp.toPx(), cy + 12.dp.toPx()),
                size = Size(52.dp.toPx(), 18.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
            drawArc(
                color = accentColor.copy(alpha = 0.5f),
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(cx - 32.dp.toPx(), cy + 16.dp.toPx()),
                size = Size(64.dp.toPx(), 22.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        "GATE" -> {
            // Jinoyat-ijroiya: Rehabilitation Portal Archway & Dawn Sunbeams
            val gateW = 40.dp.toPx()
            val gateH = 50.dp.toPx()

            drawRoundRect(
                color = accentColor.copy(alpha = 0.15f),
                topLeft = Offset(cx - gateW / 2, cy - gateH / 2),
                size = Size(gateW, gateH),
                cornerRadius = CornerRadius(gateW / 2, gateW / 2)
            )
            drawRoundRect(
                color = goldColor,
                topLeft = Offset(cx - gateW / 2, cy - gateH / 2),
                size = Size(gateW, gateH),
                cornerRadius = CornerRadius(gateW / 2, gateW / 2),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Open doors angle
            drawLine(goldColor, Offset(cx - gateW / 2, cy + 12.dp.toPx()), Offset(cx - 6.dp.toPx(), cy + 2.dp.toPx()), strokeWidth = 1.5.dp.toPx())
            drawLine(goldColor, Offset(cx + gateW / 2, cy + 12.dp.toPx()), Offset(cx + 6.dp.toPx(), cy + 2.dp.toPx()), strokeWidth = 1.5.dp.toPx())

            // Radiating light of justice in the center
            drawCircle(goldColor, radius = 5.dp.toPx(), center = Offset(cx, cy - 6.dp.toPx()))
        }

        "ROTUNDA" -> {
            // Ma'muriy sud ishlarini yuritish: Dome Rotunda of Supreme Administrative Court
            val domeW = 46.dp.toPx()
            val domeH = 26.dp.toPx()

            // Half-dome
            drawArc(
                color = accentColor.copy(alpha = 0.3f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - domeW / 2, cy - 28.dp.toPx()),
                size = Size(domeW, domeH * 2)
            )
            drawArc(
                color = goldColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - domeW / 2, cy - 28.dp.toPx()),
                size = Size(domeW, domeH * 2),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Spire
            drawLine(goldColor, Offset(cx, cy - 28.dp.toPx()), Offset(cx, cy - 36.dp.toPx()), strokeWidth = 2.dp.toPx())

            // Colonnade under dome
            listOf(-18.dp.toPx(), -6.dp.toPx(), 6.dp.toPx(), 18.dp.toPx()).forEach { xOff ->
                drawLine(goldColor, Offset(cx + xOff, cy - 2.dp.toPx()), Offset(cx + xOff, cy + 20.dp.toPx()), strokeWidth = 1.8.dp.toPx())
            }
            drawLine(goldColor, Offset(cx - 24.dp.toPx(), cy + 20.dp.toPx()), Offset(cx + 24.dp.toPx(), cy + 20.dp.toPx()), strokeWidth = 2.5.dp.toPx())
        }

        "SHIELD" -> {
            // Ma'muriy javobgarlik: Civic Protection Shield with 8-pointed Star
            val shieldPath = Path().apply {
                moveTo(cx, cy - 26.dp.toPx())
                lineTo(cx + 24.dp.toPx(), cy - 14.dp.toPx())
                lineTo(cx + 20.dp.toPx(), cy + 12.dp.toPx())
                lineTo(cx, cy + 28.dp.toPx())
                lineTo(cx - 20.dp.toPx(), cy + 12.dp.toPx())
                lineTo(cx - 24.dp.toPx(), cy - 14.dp.toPx())
                close()
            }
            drawPath(shieldPath, color = accentColor.copy(alpha = 0.25f))
            drawPath(shieldPath, color = goldColor, style = Stroke(width = 1.8.dp.toPx()))

            // Star in center
            drawCircle(goldColor, radius = 5.dp.toPx(), center = Offset(cx, cy))
        }

        "GROWTH" -> {
            // Iqtisodiy protsessual: Ascending Economic Trajectory & Commercial Gateway
            val axisBottom = cy + 20.dp.toPx()
            val axisLeft = cx - 26.dp.toPx()
            val axisRight = cx + 26.dp.toPx()

            // Coordinate axes
            drawLine(goldColor, Offset(axisLeft, cy - 22.dp.toPx()), Offset(axisLeft, axisBottom), strokeWidth = 1.5.dp.toPx())
            drawLine(goldColor, Offset(axisLeft, axisBottom), Offset(axisRight, axisBottom), strokeWidth = 1.5.dp.toPx())

            // Upward geometric growth curve
            val curvePath = Path().apply {
                moveTo(axisLeft + 4.dp.toPx(), axisBottom - 6.dp.toPx())
                lineTo(cx - 8.dp.toPx(), axisBottom - 14.dp.toPx())
                lineTo(cx + 6.dp.toPx(), axisBottom - 24.dp.toPx())
                lineTo(axisRight - 4.dp.toPx(), cy - 20.dp.toPx())
            }
            drawPath(curvePath, color = accentColor, style = Stroke(width = 2.5.dp.toPx()))

            // Node dots
            listOf(
                Offset(axisLeft + 4.dp.toPx(), axisBottom - 6.dp.toPx()),
                Offset(cx - 8.dp.toPx(), axisBottom - 14.dp.toPx()),
                Offset(cx + 6.dp.toPx(), axisBottom - 24.dp.toPx()),
                Offset(axisRight - 4.dp.toPx(), cy - 20.dp.toPx())
            ).forEach { pt ->
                drawCircle(goldColor, radius = 3.dp.toPx(), center = pt)
            }
        }

        else -> {
            // Fallback Sovereign Arch
            val archW = 44.dp.toPx()
            val archH = 50.dp.toPx()
            drawRoundRect(
                color = accentColor.copy(alpha = 0.2f),
                topLeft = Offset(cx - archW / 2, cy - archH / 2),
                size = Size(archW, archH),
                cornerRadius = CornerRadius(archW / 2, archW / 2)
            )
            drawRoundRect(
                color = goldColor,
                topLeft = Offset(cx - archW / 2, cy - archH / 2),
                size = Size(archW, archH),
                cornerRadius = CornerRadius(archW / 2, archW / 2),
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawCircle(color = goldColor, radius = 4.dp.toPx(), center = Offset(cx, cy - 6.dp.toPx()))
        }
    }
}
