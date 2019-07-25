package com.ubiquisoft.evaluation;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.ConditionType;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarDiagnosticEngine {

	public void executeDiagnostics(Car car) {
		/*
		 * Implement basic diagnostics and print results to console.
		 *
		 * The purpose of this method is to find any problems with a car's data or parts.
		 *
		 * Diagnostic Steps:
		 *      First   - Validate the 3 data fields are present, if one or more are
		 *                then print the missing fields to the console
		 *                in a similar manner to how the provided methods do.
		 *
		 *      Second  - Validate that no parts are missing using the 'getMissingPartsMap' method in the Car class,
		 *                if one or more are then run each missing part and its count through the provided missing part method.
		 *
		 *      Third   - Validate that all parts are in working condition, if any are not
		 *                then run each non-working part through the provided damaged part method.
		 *
		 *      Fourth  - If validation succeeds for the previous steps then print something to the console informing the user as such.
		 * A damaged part is one that has any condition other than NEW, GOOD, or WORN.
		 *
		 * Important:
		 *      If any validation fails, complete whatever step you are actively one and end diagnostics early.
		 *
		 * Treat the console as information being read by a user of this application. Attempts should be made to ensure
		 * console output is as least as informative as the provided methods.
		 */
		if (car == null) throw new IllegalArgumentException("Car must not be null");

        // check #1
        if (! validateCar(car)) {
            System.out.println("Yikes! Car is missing fields, cannot proceed with diagnostics.");
            return;
        }

        // check #2
        Map<PartType, Integer> missingParts = car.getMissingPartsMap();
        if (missingParts.size() > 0) {
            for (Map.Entry<PartType, Integer> entry : missingParts.entrySet()) {
                printMissingPart(entry.getKey(), entry.getValue());
            }
            System.out.println("Yikes! Car has missing parts, cannot proceed with diagnostics.");
            return;
        }

        // check #3
        if (!validateParts(car)) {
            System.out.println("Yikes! Car has damaged parts, cannot proceed with diagnostics.");
            return;
        }

        System.out.println("Success! Car passes all diagnostics.");
    }

    private void printMissingPart (PartType partType, Integer count) {
        if (partType == null) throw new IllegalArgumentException ("PartType must not be null");
        if (count == null || count <= 0) throw new IllegalArgumentException ("Count must be greater than 0");

        System.out.println (String.format ("Missing Part(s) Detected: %s - Count: %s", partType, count));
    }

    private void printDamagedPart (PartType partType, ConditionType condition) {
        if (partType == null) throw new IllegalArgumentException ("PartType must not be null");
        if (condition == null) throw new IllegalArgumentException ("ConditionType must not be null");

        System.out.println (String.format ("Damaged Part Detected: %s - Condition: %s", partType, condition));
    }

	public static void main(String[] args) throws JAXBException {

        // Load classpath resource
        InputStream xml = ClassLoader.getSystemResourceAsStream("SampleCar.xml");

        // Verify resource was loaded properly
        if (xml == null) {
            System.err.println("An error occurred attempting to load SampleCar.xml");

            System.exit(1);
        }

        // Build JAXBContext for converting XML into an Object
        JAXBContext context = JAXBContext.newInstance(Car.class, Part.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        Car car = (Car) unmarshaller.unmarshal(xml);

        // Build new Diagnostics Engine and execute on deserialized car object.

        CarDiagnosticEngine diagnosticEngine = new CarDiagnosticEngine();

        diagnosticEngine.executeDiagnostics(car);

    }

	private boolean validateCar(Car car) {

        String missingFields = "";
        boolean valid = true;

        if (car.getYear () == null) {
            missingFields += "Year ";
            valid = false;
        }
        if (car.getMake () == null) {
            missingFields += "Make ";
            valid = false;
        }
        if (car.getModel () == null) {
            missingFields += "Model ";
            valid = false;
        }
        if (! valid) {
            System.out.println (String.format ("Car is missing field(s): %s.", missingFields));
        }
        return valid;
    }

    private boolean validateParts (Car car) {

        boolean valid = true;
        List<Part> parts = car.getParts ();
        for (Part part : parts) {
            if (!part.isInWorkingCondition ()) {
                valid = false;
                printDamagedPart(part.getType(), part.getCondition());
            }
        }
        return valid;
    }

}
