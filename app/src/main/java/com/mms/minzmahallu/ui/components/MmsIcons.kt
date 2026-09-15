package com.mms.minzmahallu.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * MmsIcons — hand-crafted 24dp filled vector icons (no external icon
 * dependency, keeps the APK small and the custom-Compose architecture).
 * Silhouette style so they read cleanly at 20–40dp on tinted tiles.
 */
object MmsIcons {

    private fun build(name: String, block: androidx.compose.ui.graphics.vector.ImageVector.Builder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply(block).build()

    private fun androidx.compose.ui.graphics.vector.ImageVector.Builder.f(
        pathData: String,
        fillType: PathFillType = PathFillType.NonZero,
        fill: Color = Color(0xFFFFFFFF),
    ) {
        addPath(
            pathData = addPathNodes(pathData),
            pathFillType = fillType,
            fill = SolidColor(fill), stroke = null, strokeLineWidth = 0f,
            strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
        )
    }

    val Dashboard: ImageVector by lazy { build("MmsDashboard") {
        f("M3 3h8v8H3V3zm10 0h8v8h-8V3zM3 13h8v8H3v-8zm10 0h8v8h-8v-8z")
    } }

    val Families: ImageVector by lazy { build("MmsFamilies") {
        f("M12 2.6l9.5 7.9-1.3 1.5-1.2-1V21h-6.5v-5.5h-3V21H3V11l-1.2 1L.5 10.5 12 2.6z")
    } }

    val Members: ImageVector by lazy { build("MmsMembers") {
        f("M12 12c2.2 0 4-1.8 4-4s-1.8-4-4-4-4 1.8-4 4 1.8 4 4 4zm0 2c-3.3 0-8 1.7-8 5v2h16v-2c0-3.3-4.7-5-8-5z")
    } }

    val Staff: ImageVector by lazy { build("MmsStaff") {
        f("M20 4H4C2.9 4 2 4.9 2 6v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zM8.5 9a2 2 0 110 4 2 2 0 010-4zM12 17H5v-.7c0-1 2-1.8 3.5-1.8s3.5.8 3.5 1.8V17zm7 0h-5v-2h5v2zm0-4h-5v-2h5v2z")
    } }

    val Committee: ImageVector by lazy { build("MmsCommittee") {
        f("M9 11.5c2.2 0 4-1.8 4-4s-1.8-4-4-4-4 1.8-4 4 1.8 4 4 4zm0 2c-3.3 0-8 1.7-8 5v2h11v-2c0-1.2.8-2.4 2-3.3-1.6-.5-3.3-.7-5-.7zm7.5-2c1.9 0 3.5-1.6 3.5-3.5S18.4 4.5 16.5 4.5c-.4 0-.8.1-1.2.2 1 1 1.6 2.4 1.6 3.8s-.6 2.8-1.6 3.8c.4.1.8.2 1.2.2zm.5 2c-.6 0-1.2.1-1.8.2 1 .9 1.8 2.1 1.8 3.3v2h6v-1.5c0-2.5-4-4-6-4z")
    } }

    val Subscriptions: ImageVector by lazy { build("MmsSubscriptions") {
        f("M18 2H6c-1.1 0-2 .9-2 2v18l3-2 3 2 2-2 2 2 3-2 3 2V4c0-1.1-.9-2-2-2zM8 7h8v2H8V7zm0 4h8v2H8v-2zm0 4h5v2H8v-2z")
    } }

    val Donations: ImageVector by lazy { build("MmsDonations") {
        f("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z")
    } }

    val Accounting: ImageVector by lazy { build("MmsAccounting") {
        f("M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM8 18H6v-2h2v2zm0-4H6v-2h2v2zm0-4H6V8h2v2zm5 8h-2v-2h2v2zm0-4h-2v-2h2v2zm0-4h-2V8h2v2zm5 8h-2v-2h2v2zm0-4h-2v-2h2v2zm0-4h-2V8h2v2z")
    } }

    val Assets: ImageVector by lazy { build("MmsAssets") {
        f("M3 21V5c0-1.1.9-2 2-2h8c1.1 0 2 .9 2 2v4h4c1.1 0 2 .9 2 2v10h-7.5v-4h-3v4H3zm4-13h2V6H7v2zm0 4h2v-2H7v2zm0 4h2v-2H7v2zm4-8h2V6h-2v2zm0 4h2v-2h-2v2zm0 4h2v-2h-2v2z")
    } }

    val Marriages: ImageVector by lazy { build("MmsMarriages") {
        f("M12 8a5 5 0 110 10 5 5 0 010-10zm0 2a3 3 0 100 6 3 3 0 000-6zM17.5 3a3.5 3.5 0 110 7 3.5 3.5 0 010-7zm0 1.5a2 2 0 100 4 2 2 0 000-4z",
           PathFillType.EvenOdd)
    } }

    val Deaths: ImageVector by lazy { build("MmsDeaths") {
        f("M12 3a6 6 0 016 6v11H6V9a6 6 0 016-6zM4 20h16v1.5H4V20z")
    } }

    val Welfare: ImageVector by lazy { build("MmsWelfare") {
        // heart cradled by open hands
        f("M12 12.9s-4.9-3.2-6.4-6C4.6 4.8 5.9 2.6 8.2 2.6c1.3 0 2.8.8 3.8 2 1-1.2 2.5-2 3.8-2 2.3 0 3.6 2.2 2.6 4.3-1.5 2.8-6.4 6-6.4 6z")
        f("M12 15.2c-3.1 0-5.8 1-7.2 2.5-.4.4-.4 1.1 0 1.5.4.4 1.1.4 1.5 0 1.5-1.5 3.5-2.3 5.7-2.3s4.2.8 5.7 2.3c.4.4 1.1.4 1.5 0 .4-.4.4-1.1 0-1.5-1.4-1.5-4.1-2.5-7.2-2.5z")
    } }

    val Certificates: ImageVector by lazy { build("MmsCertificates") {
        f("M12 2a5.5 5.5 0 110 11 5.5 5.5 0 010-11zm0 2a3.5 3.5 0 100 7 3.5 3.5 0 000-7zM9.4 12.8L8 22l4-2.4L16 22l-1.4-9.2c-.8.4-1.7.6-2.6.6s-1.8-.2-2.6-.6z",
           PathFillType.EvenOdd)
    } }

    val Tokens: ImageVector by lazy { build("MmsTokens") {
        f("M4 5h16c1.1 0 2 .9 2 2v3c-1.1 0-2 .9-2 2s.9 2 2 2v3c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2v-3c1.1 0 2-.9 2-2s-.9-2-2-2V7c0-1.1.9-2 2-2zm7 2h2v2h-2V7zm0 4h2v2h-2v-2zm0 4h2v2h-2v-2z")
    } }

    val Reports: ImageVector by lazy { build("MmsReports") {
        f("M4 20h16v1.5H4V20zM6 11h3v7H6v-7zm5-6h3v13h-3V5zm5 3h3v10h-3V8z")
    } }

    val Settings: ImageVector by lazy { build("MmsSettings") {
        f("M4 7h9v2H4V7zm11.5 0H20v2h-4.5V7zM14 5a2 2 0 110 4 2 2 0 010-4zM4 15h4.5v2H4v-2zm7 0H20v2h-9v-2zm-1.5-2a2 2 0 110 4 2 2 0 010-4z")
    } }

    val Users: ImageVector by lazy { build("MmsUsers") {
        f("M12 2l8 3v6c0 5-3.4 9.4-8 11-4.6-1.6-8-6-8-11V5l8-3zm0 5.5a2.75 2.75 0 100 5.5 2.75 2.75 0 000-5.5zm0 7.5c-2.4 0-4.9 1.2-4.9 2.7v.6C8.4 19.4 10.1 20 12 20s3.6-.6 4.9-1.7v-.6c0-1.5-2.5-2.7-4.9-2.7z",
           PathFillType.EvenOdd)
    } }

    val Audit: ImageVector by lazy { build("MmsAudit") {
        f("M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2zm2 4h2v2H6V8zm0 4h2v2H6v-2zm3-4h9v2H9V8zm0 4h9v2H9v-2z")
    } }

    val Backup: ImageVector by lazy { build("MmsBackup") {
        f("M12 2C7.6 2 4 3.6 4 5.5v13C4 20.4 7.6 22 12 22s8-1.6 8-3.5v-13C20 3.6 16.4 2 12 2zm0 2c3.9 0 6 1.3 6 1.5S15.9 7 12 7 6 5.7 6 5.5 8.1 4 12 4zm6 14.5c0 .2-2.1 1.5-6 1.5s-6-1.3-6-1.5v-2.6c1.5.8 3.7 1.1 6 1.1s4.5-.3 6-1.1v2.6zm0-5c0 .2-2.1 1.5-6 1.5s-6-1.3-6-1.5v-2.6c1.5.8 3.7 1.1 6 1.1s4.5-.3 6-1.1v2.6z")
    } }

    // ---- UI glyphs --------------------------------------------------------
    val Menu: ImageVector by lazy { build("MmsMenu") {
        f("M3 6h18v2H3V6zm0 5h18v2H3v-2zm0 5h18v2H3v-2z")
    } }
    val Search: ImageVector by lazy { build("MmsSearch") {
        f("M15.5 14h-.8l-.3-.3c1-1.1 1.6-2.6 1.6-4.2C16 5.9 13.1 3 9.5 3S3 5.9 3 9.5 5.9 16 9.5 16c1.6 0 3.1-.6 4.2-1.6l.3.3v.8l5 5 1.5-1.5-5-5zm-6 0C7 14 5 12 5 9.5S7 5 9.5 5 14 7 14 9.5 12 14 9.5 14z")
    } }
    val Bell: ImageVector by lazy { build("MmsBell") {
        f("M12 22a2 2 0 002-2h-4a2 2 0 002 2zm6-6V11c0-3.1-1.6-5.6-4.5-6.3V4a1.5 1.5 0 10-3 0v.7C7.6 5.4 6 7.9 6 11v5l-2 2v1h16v-1l-2-2z")
    } }
    val Close: ImageVector by lazy { build("MmsClose") {
        f("M19 6.4L17.6 5 12 10.6 6.4 5 5 6.4 10.6 12 5 17.6 6.4 19 12 13.4 17.6 19 19 17.6 13.4 12 19 6.4z")
    } }
    val Logout: ImageVector by lazy { build("MmsLogout") {
        f("M10 3h8c1.1 0 2 .9 2 2v14c0 1.1-.9 2-2 2h-8v-2h8V5h-8V3zM8.2 7.6L9.6 9l-2 2H15v2H7.6l2 2-1.4 1.4L4 12l4.2-4.4z")
    } }
    val Sun: ImageVector by lazy { build("MmsSun") {
        f("M12 7a5 5 0 110 10 5 5 0 010-10zM11 1h2v3.5h-2V1zm0 18.5h2V23h-2v-3.5zM1 11h3.5v2H1V11zm18.5 0H23v2h-3.5v-2zM4 5.3L5.4 3.9l2.5 2.5L6.5 7.8 4 5.3zm12.1 12.3l1.4-1.4 2.5 2.5-1.4 1.4-2.5-2.5zM4 18.7l2.5-2.5 1.4 1.4-2.5 2.5L4 18.7zm12.1-12.3l2.5-2.5 1.4 1.4-2.5 2.5-1.4-1.4z")
    } }
    val Moon: ImageVector by lazy { build("MmsMoon") {
        f("M12.3 2a9 9 0 109.7 11.3A7.5 7.5 0 0112.3 2z")
    } }
    val ChevronRight: ImageVector by lazy { build("MmsChevronRight") {
        f("M9 6l6 6-6 6-1.4-1.4L12.2 12 7.6 7.4 9 6z")
    } }
    val Add: ImageVector by lazy { build("MmsAdd") {
        f("M11 5h2v6h6v2h-6v6h-2v-6H5v-2h6V5z")
    } }
    val Check: ImageVector by lazy { build("MmsCheck") {
        f("M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z")
    } }
    val Trash: ImageVector by lazy { build("MmsTrash") {
        f("M6 7h12l-1 14H7L6 7zm3-3h6l1 2h4v2H4V6h4l1-2z")
    } }

    /** Icon for a navigation destination route (WhatsApp intentionally absent). */
    fun ofRoute(route: String): ImageVector = when (route) {
        "dashboard" -> Dashboard
        "families" -> Families
        "members" -> Members
        "staff" -> Staff
        "committee" -> Committee
        "subscriptions" -> Subscriptions
        "donations" -> Donations
        "accounting" -> Accounting
        "assets" -> Assets
        "marriages" -> Marriages
        "deaths" -> Deaths
        "welfare" -> Welfare
        "certificates" -> Certificates
        "tokens" -> Tokens
        "reports" -> Reports
        "settings" -> Settings
        "users" -> Users
        "audit" -> Audit
        "backup" -> Backup
        else -> Dashboard
    }
}
