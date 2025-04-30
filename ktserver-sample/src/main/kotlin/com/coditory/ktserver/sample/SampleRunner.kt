package com.coditory.klog.sample

import com.coditory.klog.Klog

object SampleRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val l = Klog.logger<SampleRunner>()
        val e = IllegalArgumentException("Sample exception")
        l.error(e) { "Testing error" }
//        runBlocking {
//            repeat(10000) {
//                launch {
//                    l.info { "Hello there: $it" }
//                }
//            }
//        }
        l.info { "Test 123" }
        Klog.flush()
        l.info { "Test 456" }
        Thread.sleep(5000)
        println("Exit")
        Klog.stopAndFlush()
    }
}
