package com.iceteasoftware.iam.dto.request.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Author: thinhtd
 * Date: 12/4/2024
 * Time: 1:54 PM
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateWalletRequest implements Serializable {

    private String userId;

    private Long balance;

}
