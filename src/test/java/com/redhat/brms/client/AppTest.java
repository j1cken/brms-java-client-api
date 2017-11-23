package com.redhat.brms.client;

import org.drools.core.command.impl.GenericCommand;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.runtime.ExecutionResults;
import org.kie.example.project1.Dish;
import org.kie.example.project1.Guests;
import org.kie.example.project1.Season;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Unit test for simple App.
 */
public class AppTest {

    static RuleServicesClient ruleClient;

    static String url = "http://localhost:8080/kie-server/services/rest/server";
    static String username = "admin";
    static String password = "admin123";
    String container = "project1";
    /**
     * stateless Session
     */
        String session = "myStatelessSession";
    /**
     * stateful Session
     */
//    String session = "myKieSession";

    @BeforeClass
    public static void setUpBRMSConnection() {
        KieServicesConfiguration config = KieServicesFactory
                .newRestConfiguration(url, username, password);
        Set<Class<?>> allClasses = new HashSet<Class<?>>();
        allClasses.add(Guests.class);
        allClasses.add(Season.class);
        allClasses.add(Dish.class);
        config.addExtraClasses(allClasses);
        config.setMarshallingFormat(MarshallingFormat.JSON);

        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
        ruleClient = client.getServicesClient(RuleServicesClient.class);
    }

    /**
     * Rigourous Test :-)
     */
    @org.junit.Test
    public void testRoastbeefOutcome() {
        Assert.assertNotNull("rule client is null", ruleClient);

        String season = "Winter";
        String result = "Roastbeef";

        for (int numberOfGuests = 1; numberOfGuests <= 8; numberOfGuests++) {
            runTest(season, numberOfGuests, result);
        }
    }

    @org.junit.Test
    public void testStewOutcome() {
        Assert.assertNotNull("rule client is null", ruleClient);

        String season = "Winter";
        String result = "Stew";

        for (int numberOfGuests = 9; numberOfGuests <= 20; numberOfGuests++) {
            runTest(season, numberOfGuests, result);
        }
    }

    @org.junit.Test
    public void testSpareribsOutcome() {
        Assert.assertNotNull("rule client is null", ruleClient);

        String season = "Fall";
        String result = "Spareribs";

        for (int numberOfGuests = 1; numberOfGuests <= 8; numberOfGuests++) {
            runTest(season, numberOfGuests, result);
        }
    }

    private void runTest(String season, int numberOfGuests, String result) {
        ServiceResponse<ExecutionResults> response = getStringServiceResponse(season, numberOfGuests);

        assertResults(response, result);
        printResults(response);
    }

    private void assertResults(ServiceResponse<ExecutionResults> response, String expected) {
        Assert.assertNotNull("response is null", response.getResult());

        Assert.assertEquals("no housekeeping of session objects", 4, ((ArrayList) response.getResult().getValue("result")).size());

        ((ArrayList) response.getResult().getValue("result")).stream().filter(new Predicate() {
            @Override
            public boolean test(Object o) {
                return Dish.class.isAssignableFrom(o.getClass());
            }
        }).forEach(entry -> {
            Assert.assertEquals("result not expected", expected, ((Dish)entry).getName());
        });
    }

    private void printResults(ServiceResponse<ExecutionResults> response) {
        //        Assert.assertEquals(2, response.getResult().getValue("fire-identifier"));
//        response.getResult().getIdentifiers().stream().forEach(i -> {
//            System.out.println(i);
//            response.getResult().getFactHandle(i)
//            response.getResult().getValue(i);
//        });

//        System.out.println(response.getResult().getValue("result"));
//        System.out.println(response.getMsg());
//        System.out.println(response.getType());
    }

    private ServiceResponse<ExecutionResults> getStringServiceResponse(String season, int numberOfGuests) {
        List<GenericCommand<?>> commands = new ArrayList<GenericCommand<?>>();

        commands.add((GenericCommand<?>) KieServices.Factory
                .get().getCommands().newInsert(new Season(season)));
        commands.add((GenericCommand<?>) KieServices.Factory
                .get().getCommands().newInsert(new Guests(0, 0, numberOfGuests)));
        commands.add((GenericCommand<?>) KieServices.Factory
                .get().getCommands().newFireAllRules("fire-identifier"));
        commands.add((GenericCommand<?>) KieServices.Factory
                .get().getCommands().newGetObjects("result"));
//        commands.add((GenericCommand<?>) KieServices.Factory
//                .get().getCommands().newGetObjects(new ClassObjectFilter(Dish.class),"outcome"));

        BatchExecutionCommand batchCommand = KieServices.Factory
                .get().getCommands().newBatchExecution(commands, session);

        return ruleClient.executeCommandsWithResults(container, batchCommand);
    }
}
