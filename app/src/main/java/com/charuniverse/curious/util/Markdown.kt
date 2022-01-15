package com.charuniverse.curious.util

import com.charuniverse.curious.R

object Markdown {

    enum class Tag {
        BOLD,
        ITALIC,
        STRIKE_TROUGH,
        LINK,
        CHECK_BOX,
        BULLET_LIST,
        NUMBERED_LIST,
        QUOTE,
        LATEX_BLOCK,
        CODE_BLOCK,
    }

    data class Element(
        val tag: Tag,
        val suffix: String = "",
        val prefix: String = "",
        val icon: Int = R.drawable.ic_launcher_background,
        val tooltipText: String = "",
    )

    val elements = listOf(
        Element(
            tag = Tag.BOLD,
            prefix = "**",
            suffix = "**",
            icon = R.drawable.ic_baseline_format_bold_24,
            tooltipText = "Bold"
        ),
        Element(
            tag = Tag.ITALIC,
            prefix = "*",
            suffix = "*",
            icon = R.drawable.ic_baseline_format_italic_24,
            tooltipText = "Italic",
        ),
        Element(
            tag = Tag.STRIKE_TROUGH,
            prefix = "~~",
            suffix = "~~",
            icon = R.drawable.ic_baseline_format_strikethrough_24,
            tooltipText = "Strike Through",
        ),
        Element(
            tag = Tag.LINK,
            prefix = "",
            suffix = "",
            icon = R.drawable.ic_baseline_link_24,
            tooltipText = "Link",
        ),
        Element(
            tag = Tag.QUOTE,
            prefix = "> ",
            suffix = "",
            icon = R.drawable.ic_baseline_format_quote_24,
            tooltipText = "Quote",
        ),
        Element(
            tag = Tag.CHECK_BOX,
            prefix = "- [ ] ",
            suffix = "",
            icon = R.drawable.ic_baseline_check_box_24,
            tooltipText = "Check box",
        ),
        Element(
            tag = Tag.BULLET_LIST,
            prefix = "- ",
            suffix = "",
            icon = R.drawable.ic_baseline_format_list_bulleted_24,
            tooltipText = "Bullet List",
        ),
        Element(
            tag = Tag.NUMBERED_LIST,
            prefix = "1. ",
            suffix = "",
            icon = R.drawable.ic_baseline_format_list_numbered_24,
            tooltipText = "Numbered List",
        ),
        Element(
            tag = Tag.CODE_BLOCK,
            prefix = "```\n",
            suffix = "\n```\n",
            icon = R.drawable.ic_baseline_code_24,
            tooltipText = "Code Block",
        ),
        Element(
            tag = Tag.LATEX_BLOCK,
            prefix = "$$$\n",
            suffix = "\n$$$\n",
            icon = R.drawable.ic_baseline_functions_24,
            tooltipText = "Latex Block",
        ),
    )
}
