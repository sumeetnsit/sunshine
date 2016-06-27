package com.example.sumeet.sunshine;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.TestSuite;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class FullTestSuite extends TestSuite{
    public static Test suite(){
        return (Test) new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }
    public FullTestSuite(){
        super();
    }

}