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
    Mono<String> doGETRequest(String path, Map<String, String> additionalHeaders);

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

    Mono<byte[]> doPOSTRequestBinary(String path, String request, Map<String, String> additionalHeaders);

    /**
     * Выполняет GET запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат в виде массива байт, ответ даже не десериализуется в строку.
     */

    Mono<byte[]> doGETRequestBinary(String path, Map<String, String> additionalHeaders);


    /**
     * Выполняет PUT запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path    относительный uri запроса.
     * @param request тело запроса.
     * @return {@link Mono} результат.
     */
    default Mono<String> doPUTRequest(String path, String request) {
        return doPUTRequest(path, request, new HashMap<>());
    }

    /**
     * Выполняет PUT запрос к внешнему сервису по HTTP протоколу.
     *
     * @param path              относительный uri запроса.
     * @param request           тело запроса.
     * @param additionalHeaders дополнительные заголовки для этого запроса (переопределяет дублирующиеся заголовки из конфигурации).
     * @return {@link Mono} результат.
     */
    Mono<String> doPUTRequest(String path, String request, Map<String, String> additionalHeaders);

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

    Mono<String> doDELETERequest(String path,
                                 String request,
                                 String request4log,
                                 Map<String, String> additionalHeaders);

    /**
     * Возвращает используемый WebClient.
     *
     * @return WebClient.
     */
    WebClient getWebClient();

}
