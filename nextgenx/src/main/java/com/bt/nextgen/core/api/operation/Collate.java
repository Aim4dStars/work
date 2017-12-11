package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.model.ApiError;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;
import static java.lang.Math.min;

/**
 * Collate the results of multiple controller operations into a single result list.
 * @author M013938
 */
public class Collate implements ControllerOperation {

    /** Chained sequence of operations to be performed, with the results collated into a single result list. */
    private final List<ControllerOperation> operations = new ArrayList<>();

    /**
     * Constructor.
     * @param operations list of initial operations to add to the queue.
     */
    public Collate(ControllerOperation... operations) {
        this.operations.addAll(asList(operations));
    }

    /**
     * Dynamically add another operation post-construct.
     * @param operation additional operation to be performed.
     */
    public void addOperation(@Nonnull ControllerOperation operation) {
        operations.add(operation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ApiResponse performOperation() {
        final List<Dto> results = new ArrayList<>(operations.size());
        Integer status = null;
        String version = CURRENT_VERSION;
        final ApiError.Builder errorBuilder = new ApiError.Builder();
        for (ApiResponse response : performOperations()) {
            if (status == null) {
                status = response.getStatus();
            } else {
                status = min(status, response.getStatus());
            }
            if (version.compareTo(response.getApiVersion()) < 0) {
                version = response.getApiVersion();
            }
            final Dto data = response.getData();
            if (data instanceof ResultListDto) {
                results.addAll(((ResultListDto<Dto>) data).getResultList());
            } else if (data != null) {
                results.add(data);
            }
            if (response.getError() != null) {
                errorBuilder.add(response.getError());
            }
        }
        return new ApiResponse(version, status, new ResultListDto<>(results), errorBuilder.build(), null);
    }

    private ApiResponse[] performOperations() {
        // TODO: potential to multi-thread here via an executor, for now we'll keep it simple
        final int count = operations.size();
        final ApiResponse[] responses = new ApiResponse[count];
        for (int i = 0; i < count; i++) {
            responses[i] = operations.get(i).performOperation();
        }
        return responses;
    }
}
