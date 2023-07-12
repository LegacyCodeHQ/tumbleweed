package com.legacycode.eureka.dex

import com.google.common.truth.Truth.assertThat
import org.approvaltests.Approvals
import org.approvaltests.JsonApprovals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class InheritanceRegisterTest {
  private val register = InheritanceRegister()

  @Test
  fun `create an empty inheritance register`() {
    assertThat(register.isEmpty)
      .isTrue()
  }

  @Test
  fun `a registry with at least one entry is non-empty`() {
    // when
    register.add(Ancestor("Ljava/lang/Object;"), Child("Lcom/legacycode/dex/Child;"))

    // then
    assertThat(register.isEmpty)
      .isFalse()
  }

  @Test
  fun `get ancestor`() {
    // when
    register.add(Ancestor("Ljava/lang/Object;"), Child("Lcom/legacycode/dex/Child;"))

    // then
    assertThat(register.ancestors())
      .containsExactly("Ljava/lang/Object;")
  }

  @Test
  fun `get children`() {
    // when
    with(register) {
      add(Ancestor("Ljava/lang/Object;"), Child("Lcom/legacycode/dex/Child;"))
      add(Ancestor("Ljava/lang/Object;"), Child("Lcom/legacycode/dex/Node;"))
    }

    // then
    assertThat(register.children(Ancestor("Ljava/lang/Object;")))
      .containsExactly(
        Child("Lcom/legacycode/dex/Child;"),
        Child("Lcom/legacycode/dex/Node;"),
      )
  }

  @Test
  fun `return an empty set of children when parent does not exist`() {
    // when
    with(register) {
      add(Ancestor("Ljava/lang/Object;"), Child("Lcom/legacycode/dex/Child;"))
      add(Ancestor("Ljava/lang/Object;"), Child("Lcom/legacycode/dex/Node;"))
    }

    // then
    assertThat(register.children(Ancestor("Landroid/app/Activity;")))
      .isEmpty()
  }

  @Nested
  inner class Tree {
    @BeforeEach
    fun beforeEach() {
      with(register) {
        add(Ancestor("Landroid/app/Activity;"), Child("Landroidx/app/AppCompatActivity;"))
        add(Ancestor("Landroidx/app/AppCompatActivity;"), Child("Lcom/legacycode/app/BaseActivity;"))
        add(Ancestor("Lcom/legacycode/app/BaseActivity;"), Child("Landroid/app/HomeActivity;"))
        add(Ancestor("Landroidx/app/AppCompatActivity;"), Child("Lcom/legacycode/MetricsActivity;"))
        add(Ancestor("Landroid/app/Fragment;"), Child("Landroidx/app/AppCompatFragment;"))
      }
    }

    @Test
    fun `it can build a tree`() {
      // given
      val treeBuilder = TestTreeBuilder()

      // when
      val tree = register.tree(Ancestor("Landroid/app/Activity;"), treeBuilder)

      // then
      Approvals.verify(tree)
    }

    @Test
    fun `it can build a graphviz tree`() {
      // given
      val dotTreeBuilder = DotTreeBuilder("Activity")

      // when
      val graphvizTree = register.tree(Ancestor("Landroid/app/Activity;"), dotTreeBuilder)

      // then
      Approvals.verify(graphvizTree)
    }

    @Test
    fun `it can build JSON document for Tree, Cluster`() {
      // given
      val treeClusterJsonBuilder = TreeClusterJsonTreeBuilder()

      // when
      val treeClusterJson = register.tree(Ancestor("Landroid/app/Activity;"), treeClusterJsonBuilder)

      // then
      JsonApprovals.verifyJson(treeClusterJson)
    }
  }
}
