/*
 * Copyright (C) 2014-2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */
package gobblin.runtime.api;

import com.google.common.base.Preconditions;

import gobblin.runtime.JobState.RunningState;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the result of the execution of a Gobblin job.
 */
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
public class JobExecutionResult {
  private final RunningState finalState;
  private final Throwable errorCause;
  // TODO add TaskExecutionResults

  public boolean isCancelled() {
    return RunningState.CANCELLED == getFinalState();
  }

  public boolean isSuccessful() {
    return RunningState.COMMITTED == getFinalState();
  }

  public boolean isFailed() {
    return RunningState.FAILED == getFinalState();
  }

  public static JobExecutionResult createSuccessResult() {
    return new JobExecutionResult(RunningState.COMMITTED, null);
  }

  public static JobExecutionResult createFailureResult(Throwable cause) {
    return new JobExecutionResult(RunningState.FAILED, cause);
  }

  public static JobExecutionResult createCancelledResult() {
    return new JobExecutionResult(RunningState.CANCELLED, null);
  }

  public JobExecutionResult createFromState(JobExecutionState state) {
    Preconditions.checkArgument(null != state.getRunningState());
    Preconditions.checkArgument(state.getRunningState().isDone());

    if (state.getRunningState().isSuccess()) {
      return JobExecutionResult.createSuccessResult();
    }
    else if (state.getRunningState().isCancelled()) {
      return JobExecutionResult.createCancelledResult();
    }
    else {
      // FIXME we need to capture error(s)
      return JobExecutionResult.createFailureResult(
          new RuntimeException("Gobblin job failed:" + state.getJobExecution()));
    }
  }
}
