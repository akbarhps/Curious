package com.charuniverse.curious.ui.binding

import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.latex.JLatexMathTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import java.util.concurrent.Executors


@BindingAdapter("app:highlightMarkdown")
fun highlightMarkdownTextInputEditText(
    editText: TextInputEditText, isHighlighting: Boolean = false
) {
    if (!isHighlighting) return
    editText.addTextChangedListener(
        MarkwonEditorTextWatcher.withPreRender(
            MarkwonEditor.create(Markwon.create(editText.context)),
            Executors.newCachedThreadPool(), editText
        )
    )
}

@BindingAdapter("app:highlightMarkdown")
fun highlightMarkdownEditText(
    editText: EditText, isHighlighting: Boolean = false
) {
    if (!isHighlighting) return
    editText.addTextChangedListener(
        MarkwonEditorTextWatcher.withPreRender(
            MarkwonEditor.create(Markwon.create(editText.context)),
            Executors.newCachedThreadPool(), editText
        )
    )
}

@BindingAdapter("app:renderMarkdown")
fun renderMarkdown(textView: TextView, content: String?) {
    if (content == null) return
    val context = textView.context

    Markwon.builder(context)
        .usePlugin(HtmlPlugin.create())
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(TaskListPlugin.create(context))
        .usePlugin(TablePlugin.create(context))
        .usePlugin(MarkwonInlineParserPlugin.create())
        .usePlugin(customPlugin)
        .build()
        .setMarkdown(textView, content)
}

private val customPlugin = object : AbstractMarkwonPlugin() {
    override fun configureTheme(builder: MarkwonTheme.Builder) {
        builder.headingBreakHeight(0)
    }

//    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
//        builder.appendFactory(Image::class.java) { config, props ->
//            val url = ImageProps.DESTINATION.require(props)
//            LinkSpan(config.theme(), url, ImageLinkResolver(config.linkResolver()))
//                LinkSpan(config.theme(), url, config.linkResolver())
//        }
//    }
}

//private class ImageLinkResolver(private val defaultResolver: LinkResolver) : LinkResolver {
//    override fun resolve(view: View, link: String) {
//        val mimeType = URLConnection.guessContentTypeFromName(link)
//        if (mimeType.startsWith("image")) {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.setDataAndType(Uri.parse(link), "image/*")
//            intent.flags =
//                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
//            view.context.startActivity(intent)
//        } else {
//            defaultResolver.resolve(view, link)
//        }
//    }
//}
