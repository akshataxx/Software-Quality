package com.SENG4430.HalsteadComplexity;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.*;
import spoon.reflect.code.*;
import spoon.reflect.visitor.*;
import spoon.support.reflect.code.*;



public class HalsteadComplexityChk {
    private int n1, n2; //number of distinct operators and operands
    private int N1, N2; //Total number of occurrences of operators and operands

    private final Map<String, Integer> halsteadNumbers;
    private final Map<String, String> halsteadAttributes;
    private final Map<String, CtClass> ctClass;

    private final Map<String, Integer> distinctOperators;
    private final Map<String, Integer> distinctOperands;

    private final Map<String, Integer> distinctAssgnOperators;
    private final Map<String, Integer> distinctAssgnOperands;

    enum OperatorCase {
        EXISTS, NOT_EXISTS
    }
    public HalsteadComplexityChk() {
        n1 = 0;                             // the number of distinct operators
        n2 = 0;                             // the number of distinct operands
        N1 = 0;                             // the total number of operators
        N2 = 0;                             // the total number of operands


        halsteadNumbers = new LinkedHashMap<>();
        halsteadAttributes = new LinkedHashMap<>();
        ctClass = new HashMap<>();
        distinctOperators = new HashMap<>();
        distinctOperands = new HashMap<>();

        distinctAssgnOperators = new HashMap<>();
        distinctAssgnOperands = new HashMap<>();
    }
    // This method performs the Halstead Complexity Checks
    public void check (Launcher launcher) {
        // Get all the classes present in the source code
        List<CtClass<?>> classesInProgram = Query.getElements(launcher.getFactory(), new TypeFilter<>(CtClass.class));
        // For each class, perform the Halstead complexity analysis
        for (CtClass c : classesInProgram) {
            ctClass.put(c.getQualifiedName(), c);
            halsteadComplexityClassComputation(c);
        }

        // Calculate the values of n1, n2, N1, and N2 by adding values from the data structures
        n1 += distinctOperators.size();
        n1 += distinctAssgnOperators.size();
        n2 += distinctOperands.size();
        n2 += distinctAssgnOperands.size();

        for (Map.Entry<String, Integer> dOperator : distinctOperators.entrySet()) {
            N1 += dOperator.getValue();
        }

        for (Map.Entry<String, Integer> dAssgnOperator : distinctAssgnOperators.entrySet()) {
            N1 += dAssgnOperator.getValue();
        }

        for (Map.Entry<String, Integer> dOperand : distinctOperands.entrySet()) {
            N2 += dOperand.getValue();
        }

        for (Map.Entry<String, Integer> dAssgnOperand : distinctAssgnOperands.entrySet()) {
            N2 += dAssgnOperand.getValue();
        }
    }

    private void halsteadComplexityClassComputation (CtClass<?> c) {
        // Analyze operator assignments
        List<CtOperatorAssignment> assignmentOperators = c.getElements(new TypeFilter<>(CtOperatorAssignment.class));
        List<CtUnaryOperator> unaryOperators = c.getElements(new TypeFilter<>(CtUnaryOperator.class));
        List<CtBinaryOperator> binaryOperators = c.getElements(new TypeFilter<>(CtBinaryOperator.class));
        List<CtConditional> conditionalOperators = c.getElements(new TypeFilter<>(CtConditional.class));
        List<CtLambda> arrowOperators = c.getElements(new TypeFilter<>(CtLambda.class));

        // Check Assignment operator
        Iterator<CtOperatorAssignment> assgnIt = assignmentOperators.iterator();
        while (assgnIt.hasNext()) {
            CtOperatorAssignment assignOperators = assgnIt.next();
            // Check if assignment operator exists in distinctOperators map, add it with a value of 1 if it doesn't exist
            switch (distinctAssgnOperators.containsKey(assignOperators.getKind().toString()) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctAssgnOperators.get(assignOperators.getKind().toString());
                    distinctAssgnOperators.put(assignOperators.getKind().toString(), freq + 1);
                    break;
                case NOT_EXISTS:
                    distinctAssgnOperators.put(assignOperators.getKind().toString(), 1);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + distinctAssgnOperators.containsKey(assignOperators.getKind().toString()));
            }
            // Check if the condition of the current conditional statement exists as a key in distinctOperands map
            switch (distinctAssgnOperands.containsKey(assignOperators.getAssigned().toString())? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctAssgnOperands.get(assignOperators.getAssigned().toString());
                    distinctAssgnOperands.put(assignOperators.getAssigned().toString(), freq + 1);
                    break;
                case NOT_EXISTS:
                    distinctAssgnOperands.put(assignOperators.getAssigned().toString(), 1);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " +  distinctAssgnOperands.containsKey(assignOperators.getKind().toString()));
            }
        }

        // Analyze Unary operator
        Iterator<CtUnaryOperator> unaryIt= unaryOperators.iterator();
        while (unaryIt.hasNext()) {
            CtUnaryOperator unOperator = unaryIt.next();
            // Check if Unary Operator exists in distinctOperators map, add it with a value of 1 if it doesn't exist
            switch (distinctOperators.containsKey(unOperator.getKind().toString()) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctOperators.get((unOperator.getKind().toString()));
                    distinctOperators.put(unOperator.getKind().toString(), freq + 1);
                    break;
                case NOT_EXISTS:
                    distinctOperators.put(unOperator.getKind().toString(), 1);
                    break;
            }
            // Check if Unary Operator exists in distinctOperands map
            switch (distinctOperands.containsKey(unOperator.getOperand().toString()) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctOperands.get(unOperator.getOperand().toString());
                    distinctOperands.put(unOperator.getOperand().toString(), freq + 1);
                case NOT_EXISTS:
                    distinctOperators.put(unOperator.getOperand().toString(), 1);
            }
        }

        // Analyze binary operator
        Iterator<CtBinaryOperator> binaryIt = binaryOperators.iterator();
        while(binaryIt.hasNext()) {
            CtBinaryOperator biOperator = binaryIt.next();
            // Check if Binary Operator exists in distinctOperators map, add it with a value of 1 if it doesn't exist
            switch (distinctOperators.containsKey(biOperator.getKind().toString()) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctOperators.get(biOperator.getKind().toString());
                    distinctOperators.put(biOperator.getKind().toString(), freq + 1);
                case NOT_EXISTS:
                    distinctOperators.put(biOperator.getKind().toString(), 1);
            }
            // Check if binary Operator exists in distinctOperands map
            switch (distinctOperands.containsKey(biOperator.getLeftHandOperand().toString()) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctOperands.get(biOperator.getLeftHandOperand().toString());
                    distinctOperands.put(biOperator.getLeftHandOperand().toString(), freq + 1);
                case NOT_EXISTS:
                    distinctOperands.put(biOperator.getLeftHandOperand().toString(), 1);
            }
            switch (distinctOperands.containsKey(biOperator.getRightHandOperand().toString()) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctOperands.get(biOperator.getRightHandOperand().toString());
                    distinctOperands.put(biOperator.getRightHandOperand().toString(), freq + 1);
                case NOT_EXISTS:
                    distinctOperands.put(biOperator.getRightHandOperand().toString(), 1);
            }
        }
        // Analyze ternary operator
        Iterator<CtConditional> qMarkIterator = conditionalOperators.iterator();
        while (qMarkIterator.hasNext()) {
            CtConditional qMark = qMarkIterator.next();
            // Check if "COND" key exists in distinctOperators map, add it with a value of 1 if it doesn't exist
            switch ((distinctOperators.containsKey("COND")) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctOperators.get("COND");
                    distinctOperators.put("COND", freq + 1);
                    break;
                case NOT_EXISTS:
                    distinctOperators.put("COND", 1);
                    break;
            }
            // Check if the condition of the current conditional statement exists as a key in distinctOperands map
            switch (distinctOperands.containsKey(qMark.getCondition().toString()) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctOperands.get(qMark.getCondition().toString());
                    distinctOperands.put(qMark.getCondition().toString(), freq + 1);
                    break;
                case NOT_EXISTS:
                    distinctOperands.put(qMark.getCondition().toString(), 1);
                    break;
            }
            // Check if the "Else" expression of the current conditional statement exists as a key in distinctOperands map
            switch (distinctOperands.containsKey(qMark.getElseExpression().toString()) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctOperands.get(qMark.getElseExpression().toString());
                    distinctOperands.put(qMark.getElseExpression().toString(), freq + 1);
                    break;
                case NOT_EXISTS:
                    distinctOperands.put(qMark.getElseExpression().toString(), 1);
                    break;
            }
            // Check if the "then" expression of the current conditional statement exists as a key in distinctOperands map
            switch (distinctOperands.containsKey(qMark.getThenExpression().toString()) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctOperands.get(qMark.getThenExpression().toString());
                    distinctOperands.put(qMark.getThenExpression().toString(), freq + 1);
                    break;
                case NOT_EXISTS:
                    distinctOperands.put(qMark.getThenExpression().toString(), 1);
                    break;
            }
        }
        // Check if the  "->" operator exist, when body is null, expression is the operand, visa versa
        Iterator<CtLambda> arrowIterator = arrowOperators.iterator();
        while (arrowIterator.hasNext()) {
            CtLambda arrow = arrowIterator.next();

            switch ((distinctOperators.containsKey("LAMBDA")) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                case EXISTS:
                    int freq = distinctOperators.get("LAMBDA");
                    distinctOperators.put("LAMBDA", freq + 1);
                    break;
                case NOT_EXISTS:
                    distinctOperators.put("LAMBDA", 1);
                    break;
            }

            if (arrow.getExpression() != null) {
                switch ((distinctOperands.containsKey(arrow.getExpression().toString())) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                    case EXISTS:
                        int freq = distinctOperands.get(arrow.getExpression().toString());
                        distinctOperands.put(arrow.getExpression().toString(), freq + 1);
                        break;
                    case NOT_EXISTS:
                        distinctOperands.put(arrow.getExpression().toString(), 1);
                        break;
                }
            } else if (arrow.getBody() != null) {
                switch ((distinctOperands.containsKey(arrow.getBody().toString())) ? OperatorCase.EXISTS : OperatorCase.NOT_EXISTS) {
                    case EXISTS:
                        int freq = distinctOperands.get(arrow.getBody().toString());
                        distinctOperands.put(arrow.getBody().toString(), freq + 1);
                        break;
                    case NOT_EXISTS:
                        distinctOperands.put(arrow.getBody().toString(), 1);
                        break;
                }
            }
        }
    }
    public Map<String, Integer> getHalsteadNumbers(){

        halsteadNumbers.put("number of distinct operators (n1) ", n1);
        halsteadNumbers.put("number of distinct operands (n2)  ", n2);
        halsteadNumbers.put("total number of operators (N1)    ", N1);
        halsteadNumbers.put("total number of operands (N2)     ", N2);

        return halsteadNumbers;
    }
    public Map<String, String> getHalsteadAttributes(){
        int programVocabulary;
        int programLength;
        double estimatedProgramLength ;
        double volume;
        double difficulty;
        double effort;
        double timeRequiredtoProgram;
        double deliveredBugs;

        try {
            programVocabulary = n1 + n2;                        // Compute program vocabulary size
            programLength = N1 + N2;                            // Compute program length
            estimatedProgramLength  = n1 * (Math.log(n1) / Math.log(2)) + n2 * (Math.log(n2) / Math.log(2.0));// Compute estimated program length
            volume = programLength * (Math.log(n1) / Math.log(2) + Math.log(n2) / Math.log(2));     // Compute volume
            difficulty = (n1 / 2.0) * (N2 / (double) n2);                // Compute difficulty
            effort = volume * difficulty;                       // Compute effort
            timeRequiredtoProgram = effort  / 18.0;             // Compute time required to program
            deliveredBugs = volume / 3000.0;                    // Compute delivered bugs

            // Store results in a map
            halsteadAttributes.put("Program vocabulary n        ", Integer.toString(programVocabulary));
            halsteadAttributes.put("Program length N            ", Integer.toString(programLength));
            halsteadAttributes.put("Estimated program length N^ ", Double.toString(estimatedProgramLength));
            halsteadAttributes.put("Volume V                    ", Double.toString(volume));
            halsteadAttributes.put("Difficulty D                ", Double.toString(difficulty));
            halsteadAttributes.put("Effort E                    ", Double.toString(effort));
            halsteadAttributes.put("Time required to program T  ", Double.toString(timeRequiredtoProgram));
            halsteadAttributes.put("Delivered bugs B            ", Double.toString(deliveredBugs));

        }catch(Exception e){
            System.err.println("Error in computation: " + e.getMessage());
        }
        return halsteadAttributes;
    }
}