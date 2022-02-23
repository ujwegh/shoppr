package ru.nik;

import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.nik.commons.http.internal.InternalRequestExecutor;
import ru.nik.commons.http.mapper.ObjectJsonMapper;
import ru.nik.model.ProductsHelloExt;

public class ProductsClient {

    private final InternalRequestExecutor internalRequestExecutor;
    private final ObjectJsonMapper objectJsonMapper;

    public ProductsClient(InternalRequestExecutor internalRequestExecutor,
                          ObjectJsonMapper objectJsonMapper) {
        this.internalRequestExecutor = internalRequestExecutor;
        this.objectJsonMapper = objectJsonMapper;
    }

    public Mono<ProductsHelloExt> hello() {
        UriComponentsBuilder path = UriComponentsBuilder.fromPath("v1/products/hello");
        return internalRequestExecutor.doGETRequest(path.toUriString())
                .map((response) -> objectJsonMapper.toObject(response, ProductsHelloExt.class));
    }

}
