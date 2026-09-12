package id.majopay.ngateway.data.config

import org.junit.Assert.assertEquals
import org.junit.Test

class WebhookUrlBuilderTest {

    private val base = "https://api-proxy.dev-ngalehkuy.workers.dev"

    @Test
    fun `appends api key as path segment`() {
        assertEquals("$base/abc123", WebhookUrlBuilder.build(base, "abc123"))
    }

    @Test
    fun `normalizes trailing slash on base url`() {
        assertEquals("$base/abc123", WebhookUrlBuilder.build("$base/", "abc123"))
        assertEquals("$base/abc123", WebhookUrlBuilder.build("$base///", "abc123"))
    }

    @Test
    fun `trims whitespace around inputs`() {
        assertEquals("$base/abc123", WebhookUrlBuilder.build("  $base/  ", "  abc123  "))
    }

    @Test
    fun `keeps existing base path`() {
        assertEquals("$base/v1/webhook/abc123", WebhookUrlBuilder.build("$base/v1/webhook/", "abc123"))
    }

    @Test
    fun `returns base without trailing slash when api key empty`() {
        assertEquals(base, WebhookUrlBuilder.build("$base/", ""))
        assertEquals(base, WebhookUrlBuilder.build("$base/", "   "))
    }

    @Test
    fun `url encodes unsafe characters in api key`() {
        assertEquals("$base/a%2Fb%20c%3Fd", WebhookUrlBuilder.build(base, "a/b c?d"))
    }

    @Test
    fun `leaves alphanumeric dash underscore dot and tilde untouched`() {
        assertEquals("$base/mp_live-01.x~y", WebhookUrlBuilder.build(base, "mp_live-01.x~y"))
    }

    @Test
    fun `encode false keeps masked value readable`() {
        assertEquals("$base/****abcd", WebhookUrlBuilder.build("$base/", "****abcd", encode = false))
    }
}
