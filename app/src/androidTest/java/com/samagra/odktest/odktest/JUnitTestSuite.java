package com.samagra.odktest.odktest;

import com.samagra.odktest.StopOnFailureSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(StopOnFailureSuite.class)
@Suite.SuiteClasses({
        LoginActivityTest.class,
        //FillFormTest.class,



})public class JUnitTestSuite {
}
