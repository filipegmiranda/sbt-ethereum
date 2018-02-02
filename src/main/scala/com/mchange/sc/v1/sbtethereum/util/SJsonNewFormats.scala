package com.mchange.sc.v1.sbtethereum.util

import com.mchange.sc._
import v1.consuela._
import v1.consuela.ethereum.EthAddress
import v1.consuela.ethereum.encoding.{RLP,RLPSerializing}
import v1.consuela.ethereum.jsonrpc
import v1.sbtethereum.MaybeSpawnable

import sjsonnew._
import BasicJsonProtocol._

import play.api.libs.json.{Json, Format => JsFormat}

import scala.collection._


object SJsonNewFormats {

  private def rlpSerializingIso[T : RLPSerializing] = IsoString.iso(
    { ( t : T ) => RLP.encode(t).hex },
    { ( rlp : String ) => RLP.decodeComplete[T]( rlp.decodeHexAsSeq ).get }
  )

  private def playJsonSerializingIso[T : JsFormat] = IsoString.iso (
    { (t : T) => Json.stringify( Json.toJson( t ) ) },
    { ( json : String ) => Json.parse( json ).as[T] }
  )

  implicit val EthAddressIso = rlpSerializingIso[EthAddress]

  implicit val CompilationIso = playJsonSerializingIso[jsonrpc.Compilation.Contract]

  implicit val MaybeSpawnableJsFormat = Json.format[MaybeSpawnable.Seed]

  implicit val SeedIso = playJsonSerializingIso[MaybeSpawnable.Seed]

  implicit val StrigEthAddressSortedMapFormat = new JsonFormat[immutable.SortedMap[String,EthAddress]]{
    val inner = mapFormat[String,EthAddress]

    def write[J](m : immutable.SortedMap[String, EthAddress], builder : Builder[J]): Unit = {
      inner.write(m, builder)
    }
    def read[J](jsOpt : Option[J], unbuilder : Unbuilder[J]) : immutable.SortedMap[String, EthAddress] = {
      immutable.TreeMap.empty[String, EthAddress] ++ inner.read( jsOpt, unbuilder )
    }
  }

}