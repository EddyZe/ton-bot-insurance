package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.AccountsMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.BlockchainMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.DnsMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.EmulateMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.EventsMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.GaslessMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.InscriptionsMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.JettonsMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.LiteserverMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.MultisigMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.NftMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.RatesMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.SSEMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.StakingMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.StorageMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.TonconnectMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.TracesMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.UtilitiesMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.WalletMethod;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods.WebSocketMethod;

@Slf4j
public class AsyncTonapi extends AsyncTonapiClientBase {

  public AsyncTonapi(String apiKey,
      Boolean isTestnet,
      Integer maxRetries) {
    super(apiKey, isTestnet, maxRetries, null, null, null, null);
  }

  public AsyncTonapi(String apiKey,
      Boolean isTestnet,
      Integer maxRetries,
      String baseUrl,
      String websocketUrl,
      Map<String, String> headers,
      Float timeout) {
    super(apiKey, isTestnet, maxRetries, baseUrl, websocketUrl, headers, timeout);
  }

  public BlockchainMethod getBlockchain() {
    return new BlockchainMethod(this);
  }

  public AccountsMethod getAccounts() {
    return new AccountsMethod(this);
  }

  public JettonsMethod getJettons() {
    return new JettonsMethod(this);
  }

  public LiteserverMethod getLiteserver() {
    return new LiteserverMethod(this);
  }

  public MultisigMethod getMultisig() {
    return new MultisigMethod(this);
  }

  public DnsMethod getDns() {
    return new DnsMethod(this);
  }

  public EmulateMethod getEmulate() {
    return new EmulateMethod(this);
  }

  public EventsMethod getEvents() {
    return new EventsMethod(this);
  }

  public GaslessMethod getGasless() {
    return new GaslessMethod(this);
  }

  public InscriptionsMethod getInscriptions() {
    return new InscriptionsMethod(this);
  }

  public NftMethod getNft() {
    return new NftMethod(this);
  }

  public RatesMethod getRates() {
    return new RatesMethod(this);
  }

  public SSEMethod getSse() {
    return new SSEMethod(this);
  }

  public StakingMethod getStaking() {
    return new StakingMethod(this);
  }

  public StorageMethod getStorage() {
    return new StorageMethod(this);
  }

  public TonconnectMethod getTonconnect() {
    return new TonconnectMethod(this);
  }

  public TracesMethod getTraces() {
    return new TracesMethod(this);
  }

  public UtilitiesMethod getUtilities() {
    return new UtilitiesMethod(this);
  }

  public WalletMethod getWallet() {
    return new WalletMethod(this);
  }

  public WebSocketMethod getWebsocket() {
    return new WebSocketMethod(this);
  }
}
