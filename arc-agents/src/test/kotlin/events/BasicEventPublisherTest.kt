// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.events

import io.github.lmos.arc.agents.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

class BasicEventPublisherTest : TestBase() {

    @Test
    fun `test handlers receive events`() {
        val subject = BasicEventPublisher()
        val handler1 = TestHandler()
        val handler2 = OtherTestHandler()

        subject.add(handler1)
        subject.add(handler2)
        subject.publish(TestEvent())
        subject.publish(SubTestEvent())
        subject.publish(OtherTestEvent())

        assertThat(handler1.count.get()).isEqualTo(2)
        assertThat(handler2.count.get()).isEqualTo(1)
    }
}

class TestHandler : EventHandler<TestEvent> {
    val count = AtomicInteger(0)
    override fun onEvent(event: TestEvent) {
        count.incrementAndGet()
    }
}

class OtherTestHandler : EventHandler<OtherTestEvent> {
    val count = AtomicInteger(0)
    override fun onEvent(event: OtherTestEvent) {
        count.incrementAndGet()
    }
}

open class TestEvent(override val timestamp: Instant = Instant.now(), val value: String = "Test") : Event
class SubTestEvent(override val timestamp: Instant = Instant.now(), val sub: String = "Sub") : TestEvent()
class OtherTestEvent(override val timestamp: Instant = Instant.now(), val value: String = "Other") : Event
