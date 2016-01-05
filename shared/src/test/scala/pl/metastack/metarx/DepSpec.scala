package pl.metastack.metarx

import org.scalatest.{Matchers, WordSpec}

class DepSpec extends WordSpec with Matchers {
  "Dep" when {
    val x: Sub[Double] = Sub(0.0)
    val width: Sub[Double] = Sub(0.0)

    "creating simple dependencies" should {
      val right = x.dep[Double](_ + width.get, _ - width)

      "be initialized to zero" in {
        right.get should be(0.0)
      }
      "update `right` when `width` changes" in {
        width := 50.0
        width.get should be(50.0)
        right.get should be(50.0)
        x.get should be(0.0)
      }
      "update `right` when `x` changes" in {
        x := 25.0
        width.get should be(50.0)
        right.get should be(75.0)  // x + width
      }
      "update `x` when `right` changes" in {
        right := 100.0
        x.get should be(50.0)  // right - width
        width.get should be(50.0)
      }
      "update `x` and `right` when `width` changes" in {
        width := 25.0
        right.get should be(100.0)
        x.get should be(75.0)
      }
    }
    "creating more complex dependencies" should {
      val center = x.dep[Double](_ + (width.get / 2.0), _ - (width / 2.0))

      val screenX = Sub(0.0)
      val screenWidth = Sub(0.0)
      val screenCenter = screenX.dep[Double](_ + (screenWidth.get / 2.0), _ - (screenWidth / 2.0))

      "be initialized to the proper value" in {
        center.get should be(87.5)
        x.get should be(75.0)
        screenX.get should be(0.0)
        screenWidth.get should be(0.0)
        screenCenter.get should be(0.0)
      }
      "set center to screen center" in {
        center := screenCenter
        x.get should be(-12.5)
        center.get should be(0.0)
      }
      "update screen information" in {
        screenX := 10.0
        screenWidth := 500.0
        screenCenter.get should be(260.0)
        x.get should be(247.5)
        center.get should be(260.0)
      }
    }
  }
}