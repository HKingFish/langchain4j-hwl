package com.kingfish.langchain4j.service.agents;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : haowl
 * @Date : 2025/10/30 21:01
 * @Desc : 银行工具类，提供创建账户、查询余额、存款和取款等功能
 */
public class BankTool {

    private final Map<String, Double> accounts = new HashMap<>();

    public void createAccount(String user, Double initialBalance) {
        if (accounts.containsKey(user)) {
            throw new RuntimeException("Account for user " + user + " already exists");
        }
        accounts.put(user, initialBalance);
    }

    @Tool("查询指定用户的余额")
    public double getBalance(String user) {
        Double balance = accounts.get(user);
        if (balance == null) {
            throw new RuntimeException("No balance found for user " + user);
        }
        return balance;
    }

    @Tool("向指定用户存入指定金额并返回新余额")
    public Double credit(@P("user name") String user, @P("amount") Double amount) {
        Double balance = accounts.get(user);
        if (balance == null) {
            throw new RuntimeException("No balance found for user " + user);
        }
        Double newBalance = balance + amount;
        accounts.put(user, newBalance);
        return newBalance;
    }

    @Tool("从指定用户账户提取指定金额并返回新余额")
    public Double withdraw(@P("user name") String user, @P("amount") Double amount) {
        Double balance = accounts.get(user);
        if (balance == null) {
            throw new RuntimeException("No balance found for user " + user);
        }
        Double newBalance = balance - amount;
        accounts.put(user, newBalance);
        return newBalance;
    }
}