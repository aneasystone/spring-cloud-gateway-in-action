package com.stonie.springnotes;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RecorderServerHttpResponseDecorator extends ServerHttpResponseDecorator {

    private DataBufferWrapper data = null;

    public RecorderServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return DataBufferUtils.join(Flux.from(body))
                .doOnNext(d -> this.data = d)
                .flatMap(d -> super.writeWith(copy()));
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body)
                .flatMapSequential(p -> p));
    }

    public Flux<DataBuffer> copy() {
        //如果data为null 就出错了 正好可以调试
        DataBuffer buffer = this.data.newDataBuffer();
        if (buffer == null)
            return Flux.empty();

        return Flux.just(buffer);
    }
}

class DataBufferWrapper {
    private byte[] data;
    private DataBufferFactory factory;

    public DataBufferWrapper() {
    }

    public DataBufferWrapper(byte[] data, DataBufferFactory factory) {
        this.data = data;
        this.factory = factory;
    }

    public byte[] getData() {
        return data;
    }

    public DataBufferFactory getFactory() {
        return factory;
    }

    public DataBuffer newDataBuffer() {
        if (factory == null)
            return null;

        return factory.wrap(data);
    }

    public Boolean clear() {
        data = null;
        factory = null;

        return Boolean.TRUE;
    }
}