package com.bt.nextgen.core.tracking;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bt.nextgen.core.util.SETTINGS.TRACKINGREFERENCE_SESSION_HEADER;
import static com.bt.nextgen.core.util.SETTINGS.TRACKINGREFERENCE_TRANSACTION_HEADER;

/**
 * Given a http servlet request, create an appropriate tracking string
 * Implementing spec http://sharepoint.btfin.com/sites/trans/tech/architecture/Architecture%20Documents/Reference%20Documents/ESB%20-%20Common%20Service%20Attributes.docx
 */
class HttpRequestTrackingReference {

    private static final String SESSION_KEY = HttpRequestTrackingReference.class.getCanonicalName();

    private static HttpRequestTrackingReference _instance = new HttpRequestTrackingReference();

    private HttpRequestTrackingReference() {
    }

    /**
     * How you get a copy of this class.
     * @return
     */
    public static HttpRequestTrackingReference httpRequestTrackingGenerator() {
        return _instance;
    }

    /**
     * Given the current servlet request, generate a tracking id
     * @param request from the current context
     * @return a unique string that can be used for a tracking id
     */
    TrackingReference generate(HttpServletRequest request) {
        if(hasHeaders(request)) {
            return new TrackingReferenceImpl(
                    makeReference(
                            request.getHeader(TRACKINGREFERENCE_SESSION_HEADER.value())
                            , request.getHeader(TRACKINGREFERENCE_TRANSACTION_HEADER.value())));
        }
        else {
            final HttpSession session = request.getSession();
            SimluateExternal sim = (SimluateExternal) session.getAttribute(SESSION_KEY);
            if(sim == null){
                sim = new SimluateExternal();
                session.setAttribute(SESSION_KEY, sim);
            }

            return new TrackingReferenceImpl(
                    makeReference(sim.uuid, sim.transaction.getAndIncrement())
            );
        }
    }

    private boolean hasHeaders(HttpServletRequest request) {
        return request.getHeader(TRACKINGREFERENCE_SESSION_HEADER.value()) != null
                && request.getHeader(TRACKINGREFERENCE_TRANSACTION_HEADER.value()) != null;
    }

    private String makeReference(String session, String transaction){
        return makeReference(session, Integer.valueOf(transaction));
    }

    private String makeReference(String session, int transaction){
        StringBuilder str = new StringBuilder(session)
                .append(".")
                .append(String.format("%011d", transaction));

        return str.toString();
    }

    public static class SimluateExternal {
        public SimluateExternal() {
            uuid = UUID.randomUUID().toString();
            transaction = new AtomicInteger(1);
        }

        public final String uuid;
        public final AtomicInteger transaction;
    }

}
