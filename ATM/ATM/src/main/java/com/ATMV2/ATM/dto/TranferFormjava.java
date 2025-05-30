package com.ATMV2.ATM.dto;
import lombok.Data;

@Data

public class TranferFormjava {
    private String accountOrigin;
    private String accountDestination;
    private double amount;

}
