package com.iclass;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * p-urms
 * <p>
 * Created by yang.tang on 2017/2/17 15:50.
 */
public class BaseTestCase {

    public BaseTestCase() {
        MockitoAnnotations.initMocks(this);
    }
}
