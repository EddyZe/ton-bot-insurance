package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.dns.Auctions;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.dns.DNSRecord;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.domain.DomainBids;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.domain.DomainInfo;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.AsyncTonapiClientBase;

public class DnsMethod extends AsyncTonapiClientBase {

  public DnsMethod(AsyncTonapiClientBase client) {
    super(client);
  }

  /**
   * Get full information about a domain name.
   *
   * @param domainName Domain name with .ton or .t.me
   * @return CompletableFuture of DomainInfo object containing the domain information
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<DomainInfo> getInfo(String domainName) throws TONAPIError {
    String method = String.format("v2/dns/%s", domainName);
    return this.get(method, null, null, new TypeReference<DomainInfo>() {
    });
  }

  /**
   * DNS resolve for a domain name.
   *
   * @param domainName Domain name with .ton or .t.me
   * @return CompletableFuture of DNSRecord object containing the DNS records
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<DNSRecord> resolve(String domainName) throws TONAPIError {
    String method = String.format("v2/dns/%s/resolve", domainName);
    return this.get(method, null, null, new TypeReference<DNSRecord>() {
    });
  }

  /**
   * Get domain bids.
   *
   * @param domainName Domain name with .ton or .t.me
   * @return CompletableFuture of DomainBids object containing the bids for the domain
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<DomainBids> bids(String domainName) throws TONAPIError {
    String method = String.format("v2/dns/%s/bids", domainName);
    return this.get(method, null, null, new TypeReference<DomainBids>() {
    });
  }

  /**
   * Get all auctions.
   *
   * @param tld Domain filter for current auctions ("ton" or "t.me"), default is "ton"
   * @return CompletableFuture of Auctions object containing the list of auctions
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<Auctions> getAuctions(String tld) throws TONAPIError {
    String method = "v2/dns/auctions";
    Map<String, Object> params = new HashMap<>();
    params.put("tld", tld);
    return this.get(method, params, null, new TypeReference<Auctions>() {
    });
  }
}
