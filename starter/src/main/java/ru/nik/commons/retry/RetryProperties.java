package ru.nik.commons.retry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetryProperties {

    private long maxAttempts = 0;
    private long backoff = 0;
}
