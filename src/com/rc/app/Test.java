package com.rc.app;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Test
{

    public static void main(String[] aa)
    {

        try
        {
            List<String> strings = getOSWindowTitles();

            strings.forEach(System.out::println);
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
        }
    }
    public static List<String> getOSWindowTitles() throws ScriptException
    {
        List<String> osWindowTitles = new ArrayList<String>();
        String script = "tell application \"System Events\" to get " +
                "the title of every window of every process";

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("AppleScript");
        ArrayList<Object> results = (ArrayList<Object>) engine.eval(script);

        for (Object result : flatten(results)) {
            if (result != null) osWindowTitles.add(result.toString());
        }

        return osWindowTitles;
    }

    public static List<Object> flatten(Collection<Object> nested) {
        ArrayList<Object> flat = new ArrayList<Object>();
        for (Object o : nested) {
            if (o instanceof Collection) {
                flat.addAll(flatten((Collection) o));
            }
            else {
                flat.add(o);
            }
        }
        return flat;
    }
}