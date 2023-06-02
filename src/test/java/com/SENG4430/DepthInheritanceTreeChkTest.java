package com.SENG4430;

import com.SENG4430.DepthInheritanceTree.DepthInheritanceTreeChk;
import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;


public class DepthInheritanceTreeChkTest {

    @Test
    public void testCheck() {
        // Create an instance of DepthInheritanceTreeChk
        DepthInheritanceTreeChk depthInheritanceTreeChk = new DepthInheritanceTreeChk();

        // Create a launcher for your program
        Launcher launcher = new Launcher();
        launcher.addInputResource("D:/Software Quality/testResult.java");

        // Call the check method of NumberOfChildrenChkTest class with the launcher instance
        depthInheritanceTreeChk.check(launcher);

        // Assert that the depth is less than 1
        Assert.assertTrue(depthInheritanceTreeChk.getDepthInheritanceTreeCheck().size() < 1);
    }
}
