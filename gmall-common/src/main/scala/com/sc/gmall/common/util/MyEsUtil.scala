package com.sc.gmall.common.util

import java.util
import java.util.Objects

import io.searchbox.client.config.HttpClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.core.{Bulk, BulkResult, Index}

/**
  * @Autor sc
  * @DATE 0006 17:28
  */
object MyEsUtil {
  private val ES_HOST = "http://centos01"
  private val ES_HTTP_PORT = 9200
  private var factory:JestClientFactory = null

  /**
    * 获取客户端
    *
    * @return jestclient
    */
  def getClient: JestClient = {
    if (factory == null) build()
    factory.getObject
  }

  /**
    * 关闭客户端
    */
  def close(client: JestClient): Unit = {
    if (!Objects.isNull(client)) try
      client.shutdownClient()
    catch {
      case e: Exception =>
        e.printStackTrace()
    }

}
  /**
    * 建立连接
    */
  private def build(): Unit = {
    factory = new JestClientFactory
    factory.setHttpClientConfig(new HttpClientConfig.Builder(ES_HOST + ":" + ES_HTTP_PORT).multiThreaded(true)
      .maxTotalConnection(20) //连接总数
      .connTimeout(10000).readTimeout(10000).build)

  }


  //写入es,单条数据
//  private def indexDoc() ={
//    val jest: JestClient = getClient
//    val source = "{\n  \"name\":\"lisi\",\n  \"age\":123,\n  \"amount\":250.1\n}"
//    val index: Index = new Index.Builder(source).index("gmall_test").`type`("_doc").build()
//    jest.execute(index)
//    close(jest)
//  }

  //批量放入ES数据
  def indexBulk(indexName:String,list: List[Any])={
    val jest: JestClient = getClient
    val bulkBuilder = new Bulk.Builder().defaultIndex(indexName).defaultType("_doc")
    for(doc <- list){
      val index: Index = new Index.Builder(doc).build()
      bulkBuilder.addAction(index)
    }
    val items: util.List[BulkResult#BulkResultItem] = jest.execute(bulkBuilder.build()).getItems
    println(s"保存=${items.size()}")
    close(jest)
  }
}
