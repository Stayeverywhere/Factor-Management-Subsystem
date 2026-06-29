package com.factor.interfaces.rest;

import com.factor.application.trade.TraderCurrencyService;
import com.factor.common.api.ApiResponse;
import com.factor.common.model.PageResult;
import com.factor.domain.trade.TradeCurrencyAccount;
import com.factor.domain.trade.TradeTransaction;
import com.factor.interfaces.rest.dto.CurrencyOpenRequest;
import com.factor.interfaces.rest.dto.CurrencyTransferRequest;
import com.factor.interfaces.rest.dto.FreezeRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trader/currency")
public class TradeController {

    private final TraderCurrencyService traderCurrencyService;

    public TradeController(TraderCurrencyService traderCurrencyService) {
        this.traderCurrencyService = traderCurrencyService;
    }

    @GetMapping("/accounts")
    public ApiResponse<PageResult<TradeCurrencyAccount>> listAccounts(@RequestParam String traderId, @RequestParam(defaultValue = "1") long page, @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(traderCurrencyService.listAccounts(traderId, page, size));
    }

    @GetMapping("/transactions")
    public ApiResponse<List<TradeTransaction>> listTransactions(@RequestParam String accountId) {
        return ApiResponse.ok(traderCurrencyService.listTransactions(accountId));
    }

    @PostMapping("/open")
    public ApiResponse<TradeCurrencyAccount> open(@Valid @RequestBody CurrencyOpenRequest request) {
        return ApiResponse.ok(traderCurrencyService.openCurrencyAccount(request.traderId(), request.customerId(), request.customerName(), request.currency()));
    }

    @PostMapping("/freeze")
    public ApiResponse<TradeTransaction> freeze(@Valid @RequestBody FreezeRequest request) {
        return ApiResponse.ok(traderCurrencyService.freeze(request.accountId(), request.amount(), request.bizNo(), request.description()));
    }

    @PostMapping("/unfreeze")
    public ApiResponse<TradeTransaction> unfreeze(@Valid @RequestBody FreezeRequest request) {
        return ApiResponse.ok(traderCurrencyService.unfreeze(request.accountId(), request.amount(), request.bizNo(), request.description()));
    }

    @PostMapping("/transfer")
    public ApiResponse<TradeTransaction> transfer(@Valid @RequestBody CurrencyTransferRequest request) {
        return ApiResponse.ok(traderCurrencyService.transfer(request.fromAccountId(), request.toAccountId(), request.amount(), request.bizNo(), request.description()));
    }
}
