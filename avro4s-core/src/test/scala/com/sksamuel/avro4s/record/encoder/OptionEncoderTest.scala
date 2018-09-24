package com.sksamuel.avro4s.record.encoder

import com.sksamuel.avro4s.internal.{Encoder, ImmutableRecord, AvroSchema}
import org.scalatest.{Matchers, WordSpec}

class OptionEncoderTest extends WordSpec with Matchers {

  "Encoder" should {
    "support String options" in {
      case class Test(s: Option[String])
      val schema = AvroSchema[Test]
      Encoder[Test].encode(Test(Option("qwe")), schema) shouldBe ImmutableRecord(schema, Vector("qwe"))
      Encoder[Test].encode(Test(None), schema) shouldBe ImmutableRecord(schema, Vector(null))
    }
    "support boolean options" in {
      case class Test(b: Option[Boolean])
      val schema = AvroSchema[Test]
      Encoder[Test].encode(Test(Option(true)), schema) shouldBe ImmutableRecord(schema, Vector(java.lang.Boolean.valueOf(true)))
      Encoder[Test].encode(Test(None), schema) shouldBe ImmutableRecord(schema, Vector(null))
    }
  }
}

