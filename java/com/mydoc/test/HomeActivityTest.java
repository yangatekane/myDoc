package com.mydoc.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.mydoc.HomeActivity;
import com.mydoc.R;

/**
 * Created by yanga on 2013/11/18.
 */
public class HomeActivityTest extends ActivityInstrumentationTestCase2<HomeActivity> {

    private EditText pName;
    private static final String NUMBER_24 = "2 4 ENTER ";
    private static final String NUMBER_74 = "7 4 ENTER ";
    private static final String ADD_RESULT = "98";
    public HomeActivityTest() {
        super(HomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HomeActivity homeActivity = getActivity();
        pName = (EditText)homeActivity.findViewById(R.id.edit_patient_name);
    }

    public void testAddValues(){
        sendKeys(NUMBER_24);
        // now on value2 entry
        sendKeys(NUMBER_74);
        // now on Add button
        sendKeys("ENTER");
        // get result
        String matchName = pName.getText().toString();
        assertTrue("Add result should be 98", matchName.equals(ADD_RESULT));
    }
}
