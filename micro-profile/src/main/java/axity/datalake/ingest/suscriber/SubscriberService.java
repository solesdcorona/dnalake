package axity.datalake.ingest.suscriber;

import java.util.concurrent.Flow;
import java.util.stream.IntStream;


public class SubscriberService implements Flow.Subscriber<Integer> {

    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(final Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(final Integer magazineNumber) {

        subscription.request(1);
    }

    @Override
    public void onError(final Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }

}
