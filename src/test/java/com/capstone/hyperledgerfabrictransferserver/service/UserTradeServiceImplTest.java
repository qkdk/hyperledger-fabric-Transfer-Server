package com.capstone.hyperledgerfabrictransferserver.service;

import com.capstone.hyperledgerfabrictransferserver.domain.Coin;
import com.capstone.hyperledgerfabrictransferserver.domain.User;
import com.capstone.hyperledgerfabrictransferserver.domain.UserRole;
import com.capstone.hyperledgerfabrictransferserver.dto.AssetDto;
import com.capstone.hyperledgerfabrictransferserver.dto.UserTransferRequest;
import com.capstone.hyperledgerfabrictransferserver.repository.CoinRepository;
import com.capstone.hyperledgerfabrictransferserver.repository.UserRepository;
import com.capstone.hyperledgerfabrictransferserver.repository.UserTradeRepository;
import org.assertj.core.api.Assertions;
import org.hyperledger.fabric.gateway.Gateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTradeServiceImplTest {

    @InjectMocks
    UserTradeServiceImpl userTradeService;

    @Mock
    UserService userService;

    @Mock
    FabricService fabricService;

    @Mock
    UserRepository userRepository;

    @Mock
    CoinRepository coinRepository;

    @Mock
    UserTradeRepository userTradeRepository;

    @Test
    @DisplayName("유저간 송금 테스트")
    void transfer_을_테스트한다() throws Exception{
        //given
        UserTransferRequest userTransferRequest = UserTransferRequest.builder()
                .studentId(2L)
                .coinName("test")
                .amount(100L)
                .build();
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        Coin coin = Coin.of("test");
        User sender = User.of(
                1L,
                "test",
                UserRole.ROLE_USER,
                "test"
        );
        User receiver = User.of(
                2L,
                "test",
                UserRole.ROLE_USER,
                "test2"
        );
        Gateway gateway = mock(Gateway.class);
        HashMap<String, String> coinMap = new HashMap<>();
        coinMap.put("test", "100");

        when(coinRepository.findByName(any()))
                .thenReturn(Optional.of(coin));
        when(userService.getUserByJwtToken(any()))
                .thenReturn(sender);
        when(userRepository.findByStudentId(any()))
                .thenReturn(Optional.of(receiver));
        when(fabricService.getGateway())
                .thenReturn(gateway);
        when(fabricService.submitTransaction(any(), any(), any()))
                .thenReturn(
                        (Object) AssetDto.builder()
                                .assetId("asset1")
                                .owner("test")
                                .coin(coinMap)
                                .sender("test")
                                .receiver("test2")
                                .amount("100")
                );

        //when
        AssetDto assetDto = userTradeService.transfer(httpServletRequest, userTransferRequest);

        //then
        Assertions.assertThat(assetDto.getAmount()).isEqualTo(100);

    }
}