package org.apache.helix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.List;

public class TestListener extends TestListenerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onConfigurationSuccess(ITestResult tr) {
        super.onConfigurationSuccess(tr);
        if (tr.getInstanceName() != null) {
            LOG.info("******************** configuration method [{}] succeeded for [{}] ********************", tr.getName(), tr.getInstanceName());
        } else {
            LOG.info("******************** configuration method [{}] succeeded ********************", tr.getName());
        }
    }

    @Override
    public void onConfigurationFailure(ITestResult tr) {
        super.onConfigurationFailure(tr);
        if (tr.getInstanceName() != null) {
            LOG.error("******************** configuration method [{}] FAILED for [{}] ********************", tr.getName(), tr.getInstanceName());
        } else {
            LOG.error("******************** configuration method [{}] FAILED ********************", tr.getName());
        }
        if (tr.getThrowable() != null) {
            Throwable throwable = tr.getThrowable();
            LOG.error(String.format("******************** configuration method [%s] FAILED with message: %s ********************", tr.getName(), throwable.getMessage()), throwable);
        }
    }

    @Override
    public void onConfigurationSkip(ITestResult tr) {
        super.onConfigurationSkip(tr);
        if (tr.getInstanceName() != null) {
            LOG.warn("******************** configuration method [{}] skipped for [{}] ********************", tr.getName(), tr.getInstanceName());
        } else {
            LOG.warn("******************** configuration method [{}] skipped ********************", tr.getName());
        }
    }

    @Override
    public void beforeConfiguration(ITestResult tr) {
        super.beforeConfiguration(tr);
        if (tr.getInstanceName() != null) {
            LOG.info("******************** configuration method [{}] started for [{}] ********************", tr.getName(), tr.getInstanceName());
        } else {
            LOG.info("******************** configuration method [{}] started ********************", tr.getName());
        }
    }

    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        LOG.info("******************** starting suite [{}] ********************", testContext.getSuite().getName());
    }

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        LOG.info("******************** finished suite [{}] ********************", testContext.getSuite().getName());
    }

    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
        LOG.info("******************** starting test [{}] ********************", tr.getName());
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        LOG.info("******************** finished test [{}] was a success ********************", tr.getName());
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        LOG.error("******************** finished test [{}] was a FAILURE ********************", tr.getName());
        if (tr.getThrowable() != null) {
            Throwable throwable = tr.getThrowable();
            LOG.error(String.format("******************** test [%s] contained an exception with message: %s ********************", tr.getName(), throwable.getMessage()), throwable);
        }
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        String test = tr.getName();
        LOG.warn("******************** finished test [{}]: was a skipped  ********************", test);

        List<ITestNGMethod> skipCausedBy = tr.getSkipCausedBy();
        for (ITestNGMethod skipped : skipCausedBy) {
            LOG.warn("******************** test skip for [{}] was caused by [{}]", test, skipped.getQualifiedName());
        }
    }
}
