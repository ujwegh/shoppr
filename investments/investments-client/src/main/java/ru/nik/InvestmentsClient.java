package ru.nik;

import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.nik.commons.http.internal.InternalRequestExecutor;
import ru.nik.commons.http.mapper.ObjectJsonMapper;
import ru.nik.model.InvestmentsHelloExt;

public class InvestmentsClient {

    private final InternalRequestExecutor internalRequestExecutor;
    private final ObjectJsonMapper objectJsonMapper;

    public InvestmentsClient(InternalRequestExecutor internalRequestExecutor,
                             ObjectJsonMapper objectJsonMapper) {
        this.internalRequestExecutor = internalRequestExecutor;
        this.objectJsonMapper = objectJsonMapper;
    }

    public Mono<InvestmentsHelloExt> hello() {
        UriComponentsBuilder path = UriComponentsBuilder.fromPath("/v1/investments/hello");
        return internalRequestExecutor.doGETRequest(path.toUriString())
                .map((response) -> objectJsonMapper.toObject(response, InvestmentsHelloExt.class));
    }

}
