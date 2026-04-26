package com.pehrs.cb.core.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pehrs.cb.port.in.CatalogApi;
import com.pehrs.cb.port.in.spi.CatalogProvider;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogProviderSchedulerTest {

  @Mock CatalogApi catalogApi;
  @Mock CatalogProvider providerA;
  @Mock CatalogProvider providerB;

  @Test
  void refreshAll_callsRefreshOnEveryProvider() throws Exception {
    when(providerA.getId()).thenReturn("provider-a");
    when(providerB.getId()).thenReturn("provider-b");
    CatalogProviderScheduler scheduler =
        new CatalogProviderScheduler(List.of(providerA, providerB), catalogApi);

    scheduler.refreshAll();

    verify(providerA).refresh(catalogApi);
    verify(providerB).refresh(catalogApi);
  }

  @Test
  void refreshAll_callsProvidersInRegistrationOrder() throws Exception {
    when(providerA.getId()).thenReturn("provider-a");
    when(providerB.getId()).thenReturn("provider-b");
    CatalogProviderScheduler scheduler =
        new CatalogProviderScheduler(List.of(providerA, providerB), catalogApi);

    scheduler.refreshAll();

    InOrder order = inOrder(providerA, providerB);
    order.verify(providerA).refresh(catalogApi);
    order.verify(providerB).refresh(catalogApi);
  }

  @Test
  void refreshAll_continuesAfterOneProviderFails() throws Exception {
    when(providerA.getId()).thenReturn("provider-a");
    when(providerB.getId()).thenReturn("provider-b");
    doThrow(new RuntimeException("LDAP unreachable")).when(providerA).refresh(catalogApi);

    CatalogProviderScheduler scheduler =
        new CatalogProviderScheduler(List.of(providerA, providerB), catalogApi);

    // must not propagate the exception
    assertThatCode(scheduler::refreshAll).doesNotThrowAnyException();

    verify(providerA).refresh(catalogApi);
    verify(providerB).refresh(catalogApi); // still called despite providerA failure
  }

  @Test
  void refreshAll_withNoProviders_doesNotThrow() {
    CatalogProviderScheduler scheduler =
        new CatalogProviderScheduler(List.of(), catalogApi);

    assertThatCode(scheduler::refreshAll).doesNotThrowAnyException();
    verify(catalogApi, never()).upsertUser(org.mockito.ArgumentMatchers.any());
  }
}
