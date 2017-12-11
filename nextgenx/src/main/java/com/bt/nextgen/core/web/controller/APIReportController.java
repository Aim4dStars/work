package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.util.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_CURRENT_MODULE_API_PUBLIC, produces=TEXT_HTML_VALUE)
class APIReportController {

    private static final Logger logger = LoggerFactory.getLogger(APIReportController.class);

    private final RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public APIReportController(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @RequestMapping(value="/apidashboard", method = RequestMethod.GET)
    @ResponseBody
    public String show() {
        String result = "";
        logger.debug("Starting APIReportController.show()");
        if(Environment.isProduction()){
            throw new AccessDeniedException("You are not authorised");
        }

        Map<RequestMappingInfo, HandlerMethod> entries =  handlerMapping.getHandlerMethods();

        Map<RequestMappingInfo, HandlerMethod> spaEntries = filterEntriesOnReturnType(entries, "ApiResponse");
        spaEntries.putAll(filterEntriesOnReturnType(entries, "AjaxResponse"));
        Map<RequestMappingInfo, HandlerMethod> jspEntries = filterEntriesOnReturnType(entries, "ResponseEntity");
        jspEntries.putAll(filterEntriesOnReturnType(entries, "ModelAndView"));
        Map<RequestMappingInfo, HandlerMethod> apiEntries = filterEntriesOnAPIPattern(entries, "api");
        Map<RequestMappingInfo, HandlerMethod> remainingEntries = takeAway(takeAway(takeAway(entries, spaEntries), jspEntries), apiEntries);
        Map<RequestMappingInfo, HandlerMethod> coreControllers = filterEntriesOnClassPackage(remainingEntries, "com.bt.nextgen.core.web.controller");
        Map<RequestMappingInfo, HandlerMethod> remnantEntries = takeAway(remainingEntries, coreControllers);

        result += reportHeader();

        result += generateReport("Compliant(ish) APIs", apiEntries, "compliantapi");
        result += generateReport("Spring APIs", spaEntries, "springapi");
        result += generateReport("Core Controllers", coreControllers, "corecontrollers");
        result += generateReport("Other APIs", remnantEntries, "remnantapis");
        result += generateReport("JSP APIs", jspEntries, "jspapi");

        result += reportFooter();
        logger.debug("Finished APIReportController.show()");
        return result;
    }


    private Map<RequestMappingInfo,HandlerMethod> filterEntriesOnClassPackage(Map<RequestMappingInfo, HandlerMethod> entries, String packagePattern) {
        Map<RequestMappingInfo,HandlerMethod> result = new HashMap<>();
        for (RequestMappingInfo key: entries.keySet()) {
            HandlerMethod handlerMethod = entries.get(key);
            String fullClassName = handlerMethod.getBeanType().getCanonicalName();
            logger.debug("filterEntriesOnClassPackage() {} in {}", packagePattern, fullClassName);
            if (fullClassName.contains(packagePattern) ) {
                result.put(key, handlerMethod);
            }
        }
        return result;
    }

    private Map<RequestMappingInfo,HandlerMethod> filterEntriesOnAPIPattern(Map<RequestMappingInfo, HandlerMethod> entries, String apiPattern) {
        Map<RequestMappingInfo,HandlerMethod> result = new HashMap<>();
        for (RequestMappingInfo key: entries.keySet()) {
            Set<String> apiPaths = key.getPatternsCondition().getPatterns();
            for (String apiPath: apiPaths) {
                if (apiPath.contains(apiPattern) ) {
                    result.put(key, entries.get(key));
                }
            }
        }
        return result;
    }

    private Map<RequestMappingInfo,HandlerMethod> filterEntriesOnReturnType(Map<RequestMappingInfo, HandlerMethod> entries, String pattern) {
        Map<RequestMappingInfo,HandlerMethod> result = new HashMap<>();
        for (RequestMappingInfo key: entries.keySet()) {
            HandlerMethod handlerMethod = entries.get(key);
            String returnType = handlerMethod.getReturnType().getParameterType().getCanonicalName();
            logger.debug("filterEntriesOnReturnType({}) for {}", pattern, returnType);
            if (returnType.contains(pattern) ) {
                result.put(key, handlerMethod);
            }
        }
        return result;
    }

    private String generateReport(String heading, Map<RequestMappingInfo, HandlerMethod> entriesParam, String anchor) {
        SortedSet<Map.Entry<RequestMappingInfo, HandlerMethod>> entries = new TreeSet<>(comparator);
        entries.addAll(entriesParam.entrySet());
        String result = "";
        result += "<br/>";
        result += "<h2 id=\""+anchor+"\">"+heading+"</h2>";
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry: entries) {
            RequestMappingInfo key = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            result += "<div >";
            result += "<hr >";
            result += "<p ><strong >" + handlerMethod + "</strong ></p >";
            result += "</div >";
            result += "<div class=\"span-3 colborder\" >";
            result += "<p >";
            result += "<span class=\"alt\" > API Path(s):</span >";
            if (!key.getPatternsCondition().getPatterns().isEmpty()) {
                result += key.getPatternsCondition().getPatterns();
            }
            result += "</p >";
            result += "</div >";
        }
        return result;
    }

    private String reportHeader() {
        String result = "";
        result += "<link rel=\"stylesheet\" href=\"/ng/public/static/_2UgmSrNl14dx8aug05RXX9rCoE=/css/normalize.css\" type=\"text/css\"/>";
        result += "<link rel=\"stylesheet\" href=\"/ng/public/static/_3pnoynBqncDWc9VmVVUrxj3Go=/css/screen.css\" type=\"text/css\" id=\"stylesheet-2560\"/>";

        result += "<div class=\"container\">";
        result += "<div class=\"container\">";
        result += "<h1>Panorama Endpoint Dashboard</h1>";

        result += "This has three purposes: <br/>1. Am I building a duplicate API? <br/>2. What APIs are there? <br/>3. Does my API Comply to the API Path standard?<br/>";
        result += "Where the API standard is: /{public, secure}/api/module/version/featurename[/parameter] eg /public/api/nextgen/v1_0/systeminfo/infoparameter<br/>";

        result += "<a href=\"#compliantapi\">Compliant APIs</a><br/>";
        result += "<a href=\"#springapi\">Spring APIs</a><br/>";
        result += "<a href=\"#corecontrollers\">Core Controllers</a><br/>";
        result += "<a href=\"#remnantapis\">Other APIs</a><br/>";
        result += "<a href=\"#jspapi\">JSP APIs</a><br/>";

        return result;
    }


    private String reportFooter() {
        String result = "";
        result += "</div>";
        result += "</div>";
        return result;
    }

    /**
     * Set difference
     * @param superset The larger set you're taking away from
     * @param subset The smaller set you're taking away
     * @return Map of the difference
     */
    private <K,V> Map<K,V> takeAway(Map<K,V> superset, Map<K,V> subset) {
        Map<K,V> result = new HashMap<>();
        result.putAll(superset);
        for (K subsetKey:subset.keySet()) {
            if (result.containsKey(subsetKey)) {
                result.remove(subsetKey);
            }
        }
        return result;
    }

    private static Comparator<Map.Entry<RequestMappingInfo, HandlerMethod>> comparator =
        new Comparator<Map.Entry<RequestMappingInfo, HandlerMethod>>() {
        @Override
        public int compare(Map.Entry<RequestMappingInfo, HandlerMethod> o1, Map.Entry<RequestMappingInfo, HandlerMethod> o2) {
            HandlerMethod o1handlerMethod = o1.getValue();
            String o1ClassName = o1handlerMethod.getBeanType().getCanonicalName();
            HandlerMethod o2handlerMethod = o2.getValue();
            String o2ClassName = o2handlerMethod.getBeanType().getCanonicalName();
            return o1ClassName.compareTo(o2ClassName);
        }
    };

}
