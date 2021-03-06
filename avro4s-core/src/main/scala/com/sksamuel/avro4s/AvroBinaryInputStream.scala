package com.sksamuel.avro4s

import java.io.{EOFException, InputStream}

import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericDatumReader, GenericRecord}
import org.apache.avro.io.DecoderFactory

import scala.util.Try

/**
  * An implementation of [[AvroInputStream]] that reads values of type T
  * written as binary data.
  * See https://avro.apache.org/docs/current/spec.html#binary_encoding
  *
  * In order to convert the underlying binary data into types of T, this
  * input stream requires an instance of Decoder.
  */
class AvroBinaryInputStream[T](in: InputStream,
                               writerSchema: Schema,
                               readerSchema: Schema)
                              (implicit decoder: Decoder[T]) extends AvroInputStream[T] {

  private val datumReader = new GenericDatumReader[GenericRecord](writerSchema, readerSchema, new GenericData)
  private val avroDecoder = DecoderFactory.get().binaryDecoder(in, null)

  private val _iter = Iterator.continually {
    try {
      datumReader.read(null, avroDecoder)
    } catch {
      case _: EOFException => null
    }
  }.takeWhile(_ != null)

  /**
    * Returns an iterator for the values of T in the stream.
    */
  override def iterator: Iterator[T] = new Iterator[T] {
    override def hasNext: Boolean = _iter.hasNext
    override def next(): T = decoder.decode(_iter.next, readerSchema)
  }

  /**
    * Returns an iterator for values of Try[T], so that any
    * decoding issues are wrapped.
    */
  override def tryIterator: Iterator[Try[T]] = new Iterator[Try[T]] {
    override def hasNext: Boolean = _iter.hasNext
    override def next(): Try[T] = Try(decoder.decode(_iter.next, readerSchema))
  }

  override def close(): Unit = in.close()
}