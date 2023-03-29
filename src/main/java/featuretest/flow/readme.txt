Flow
Interrelated interfaces and static methods for establishing flow-controlled components in which Publishers produce items consumed by one or more Subscribers, each managed by a Subscription.

Flow.Processor<T,R>
A component that acts as both a `Subscriber` and `Publisher`.

Flow.Publisher<T>
A producer of items (and related control messages) received by Subscribers.

Flow.Subscriber<T>
A receiver of messages.

Flow.Subscription
Message control linking a Flow.Publisher and Flow.Subscriber.