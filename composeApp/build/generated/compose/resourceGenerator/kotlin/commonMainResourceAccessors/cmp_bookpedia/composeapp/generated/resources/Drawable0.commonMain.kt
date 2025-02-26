@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package cmp_bookpedia.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi

private object CommonMainDrawable0 {
  public val book_error_2: DrawableResource by 
      lazy { init_book_error_2() }

  public val compose_multiplatform: DrawableResource by 
      lazy { init_compose_multiplatform() }
}

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("book_error_2", CommonMainDrawable0.book_error_2)
  map.put("compose_multiplatform", CommonMainDrawable0.compose_multiplatform)
}

internal val Res.drawable.book_error_2: DrawableResource
  get() = CommonMainDrawable0.book_error_2

private fun init_book_error_2(): DrawableResource =
    org.jetbrains.compose.resources.DrawableResource(
  "drawable:book_error_2",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/cmp_bookpedia.composeapp.generated.resources/drawable/book_error_2.xml", -1, -1),
    )
)

internal val Res.drawable.compose_multiplatform: DrawableResource
  get() = CommonMainDrawable0.compose_multiplatform

private fun init_compose_multiplatform(): DrawableResource =
    org.jetbrains.compose.resources.DrawableResource(
  "drawable:compose_multiplatform",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/cmp_bookpedia.composeapp.generated.resources/drawable/compose-multiplatform.xml", -1, -1),
    )
)
