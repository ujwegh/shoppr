package ru.nik.commons.http;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Интерфейс клиента к внешнему сервису по HTTP протоколу.
 */
public interface HttpRequestExecutor {
    /**
     * Выполняет GET запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path относительный uri запроса.
     * @return {@link Mono} результат.
     */
    default Mono<String> doGETRequest(String path) {
        return doGETRequest(path, new HashMap<>());
    }

    /**
     * Выполняет GET запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат.
     */
    default Mono<String> doGETRequest(String path, Map<String, String> additionalHeaders) {
        return doGETRequest(path, null, null, additionalHeaders);
    }

    /**
     * Выполняет GET запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path    относительный uri запроса.
     * @param request тело запроса.
     * @return {@link Mono} результат.
     */
    default Mono<String> doGETRequest(String path, String request) {
        return doGETRequest(path, request, request, new HashMap<>());
    }

    /**
     * Выполняет GET запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path        относительный uri запроса.
     * @param request     тело запроса.
     * @param request4log тело запроса которое будет залоггировано.
     * @return {@link Mono} результат.
     */
    default Mono<String> doGETRequest(String path, String request, String request4log) {
        return doGETRequest(path, request, request4log, new HashMap<>());
    }

    /**
     * Выполняет GET запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param request           тело запроса.
     * @param request4log       тело запроса которое будет залоггировано.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат.
     */
    Mono<String> doGETRequest(String path, String request, String request4log, Map<String, String> additionalHeaders);

    /**
     * Выполняет POST запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path    относительный uri запроса.
     * @param request тело запроса.
     * @return {@link Mono} результат.
     */
    default Mono<String> doPOSTRequest(String path, String request) {
        return doPOSTRequest(path, request, new HashMap<>());
    }

    /**
     * Выполняет POST запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param request           тело запроса.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат.
     */
    Mono<String> doPOSTRequest(String path, String request, Map<String, String> additionalHeaders);

    /**
     * Выполняет POST запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param request           тело запроса.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат.
     */
    Mono<String> doPOSTRequest(String path, byte[] request, Map<String, String> additionalHeaders);

//    /**
//     * Выполняет POST запрос к внешнему сервису по HTTP протоколу.
//     *
//     * @param path        относительный uri запроса.
//     * @param request     тело запроса.
//     * @param request4log тело запроса которое будет залоггировано.
//     * @return {@link Mono} результат.
//     * <p>
//     * Бизнес сервис, который пользуется данным клиентом, не должен заботиться о том, как будет залогированно тело ответа.
//     * Используй {@link HttpRequestExecutor#doPOSTRequest(java.lang.String, java.lang.String)}
//     */
//    @Deprecated
//    default Mono<String> doPOSTRequest(String path, String request, String request4log) {
//        return doPOSTRequest(path, request, request4log, new HashMap<>());
//    }

//    /**
//     * Выполняет POST запрос к внешнему сервису по HTTP протоколу.
//     *
//     * @param path              относительный uri запроса.
//     * @param request           тело запроса.
//     * @param request4log       тело запроса которое будет залоггировано.
//     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
//     * @return {@link Mono} результат.
//     * <p>
//     * Бизнес сервис, который пользуется данным клиентом, не должен заботиться о том, как будет залогированно тело ответа.
//     * Используй {@link HttpRequestExecutor#doPOSTRequest(java.lang.String, java.lang.String, Map)}
//     */
//    @Deprecated
//    Mono<String> doPOSTRequest(String path, String request, String request4log, Map<String, String> additionalHeaders);
//
//    /**
//     * Выполняет POST запрос к внешнему сервису по HTTP протоколу.
//     *
//     * @param path              относительный uri запроса.
//     * @param request           тело запроса.
//     * @param request4log       тело запроса которое будет залоггировано.
//     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
//     * @return {@link Mono} результат.
//     * <p>
//     * Бизнес сервис, который пользуется данным клиентом, не должен заботиться о том, как будет залогированно тело ответа.
//     * Используй {@link HttpRequestExecutor#doPOSTRequest(java.lang.String, byte[], Map)}
//     */
//    @Deprecated
//    Mono<String> doPOSTRequest(String path, byte[] request, String request4log, Map<String, String> additionalHeaders);

    /**
     * Выполняет POST запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param request           тело запроса.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат.
     * <p>
     * Бизнес сервис, который пользуется данным клиентом, не должен заботиться о том, как будет залогированно тело ответа.
     */
    Mono<String> doPOSTMultiPartRequest(String path, MultiValueMap<String, HttpEntity<?>> request, Map<String, String> additionalHeaders);

    /**
     * Выполняет POST запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param request           тело запроса.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат в виде массива байт, ответ даже не десериализуется в строку.
     */

    default Mono<byte[]> doPOSTRequestBinary(String path, String request, Map<String, String> additionalHeaders) {
        return doPOSTRequestBinary(path, request, request, additionalHeaders);
    }

    /**
     * Выполняет GET запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат в виде массива байт, ответ даже не десериализуется в строку.
     */

    Mono<byte[]> doGETRequestBinary(String path, Map<String, String> additionalHeaders);

    /**
     * Выполняет POST запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param request           тело запроса.
     * @param request4log       тело запроса которое будет залоггировано.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат в виде массива байт, ответ даже не десериализуется в строку.
     * <p>
     * Бизнес сервис, который пользуется данным клиентом, не должен заботиться о том, как будет залогированно тело ответа.
     * Используй {@link HttpRequestExecutor#doPOSTRequestBinary(java.lang.String, java.lang.String, Map)}
     */

    @Deprecated
    Mono<byte[]> doPOSTRequestBinary(String path, String request, String request4log, Map<String, String> additionalHeaders);


    /**
     * Выполняет PUT запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path    относительный uri запроса.
     * @param request тело запроса.
     * @return {@link Mono} результат.
     */
    default Mono<String> doPUTRequest(String path, String request) {
        return doPUTRequest(path, request, request);
    }

    /**
     * Выполняет PUT запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path        относительный uri запроса.
     * @param request     тело запроса.
     * @param request4log тело запроса которое будет залоггировано.
     * @return {@link Mono} результат.
     * <p>
     * Бизнес сервис, который пользуется данным клиентом, не должен заботиться о том, как будет залогированно тело ответа.
     * Используй {@link HttpRequestExecutor#doPUTRequest(java.lang.String, java.lang.String)}
     */
    @Deprecated
    default Mono<String> doPUTRequest(String path, String request, String request4log) {
        return doPUTRequest(path, request, request4log, new HashMap<>());
    }

    /**
     * Выполняет PUT запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param request           тело запроса.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат.
     */
    default Mono<String> doPUTRequest(String path, String request, Map<String, String> additionalHeaders) {
        return doPUTRequest(path, request, request, additionalHeaders);
    }

    /**
     * Выполняет PUT запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param request           тело запроса.
     * @param request4log       тело запроса которое будет залоггировано.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат.
     * <p>
     * Бизнес сервис, который пользуется данным клиентом, не должен заботиться о том, как будет залогированно тело ответа.
     * Используй {@link HttpRequestExecutor#doPUTRequest(java.lang.String, java.lang.String, Map)}
     */
    @Deprecated
    Mono<String> doPUTRequest(String path, String request, String request4log, Map<String, String> additionalHeaders);

    /**
     * Выполняет DELETE запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path относительный uri запроса.
     * @return {@link Mono} результат.
     */
    default Mono<String> doDELETERequest(String path) {
        return doDELETERequest(path, new HashMap<>());
    }

    /**
     * Выполняет DELETE запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат.
     */
    Mono<String> doDELETERequest(String path, Map<String, String> additionalHeaders);

    /**
     * Возвращает используемый WebClient.
     *
     * @return WebClient.
     */
    WebClient getWebClient();

    Mono<String> doDELETERequest(String path,
                                 String request,
                                 String request4log,
                                 Map<String, String> additionalHeaders);
}
