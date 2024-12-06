package com.iceteasoftware.wallet.dto.request.wallet;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 10:30 AM
 */
@Getter
@Setter
public class CreateWalletRequest implements Serializable {

    private String userId;

    private Long currency;
}
