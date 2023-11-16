package org.apache.helix.metaclient.recipes.lock;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.helix.metaclient.api.MetaClientInterface;
import org.apache.helix.metaclient.factories.MetaClientConfig;
import org.apache.helix.metaclient.impl.zk.ZkMetaClientTestBase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LockClientTest extends ZkMetaClientTestBase {

  private static final String TEST_INVALID_PATH = "/_invalid/a/b/c";
  private static final String OWNER_ID = "urn:li:principal:UNKNOWN";
  private static final String CLIENT_ID = "test_client_id";
  private static final String CLIENT_DATA = "client_data";
  private static final String LOCK_ID = "794c8a4c-c14b-4c23-b83f-4e1147fc6978";
  private static final long GRANT_TIME = System.currentTimeMillis();
  private static final long LAST_RENEWAL_TIME = System.currentTimeMillis();
  private static final long TIMEOUT = 100000;

  public LockClient createLockClient() {

    MetaClientConfig.StoreType storeType = MetaClientConfig.StoreType.ZOOKEEPER;
    MetaClientConfig config = new MetaClientConfig.MetaClientConfigBuilder<>().setConnectionAddress(ZK_ADDR)
        .setStoreType(storeType).build();
    return new LockClient(config);
  }

  private LockInfo createLockInfo() {
    LockInfo lockInfo = new LockInfo();
    lockInfo.setOwnerId(OWNER_ID);
    lockInfo.setClientId(CLIENT_ID);
    lockInfo.setClientData(CLIENT_DATA);
    lockInfo.setLockId(LOCK_ID);
    lockInfo.setGrantedAt(GRANT_TIME);
    lockInfo.setLastRenewedAt(LAST_RENEWAL_TIME);
    lockInfo.setTimeout(TIMEOUT);
    return lockInfo;
  }

  @Test
  public void testAcquireLock() {
    final String key = "/TestLockClient_testAcquireLock";
    try (LockClient lockClient = createLockClient()) {
      LockInfo lockInfo = createLockInfo();
      lockClient.acquireLock(key, lockInfo, MetaClientInterface.EntryMode.PERSISTENT);
      Assert.assertNotNull(lockClient.retrieveLock(key));
      try {
        lockClient.acquireLock(TEST_INVALID_PATH, new LockInfo(), MetaClientInterface.EntryMode.PERSISTENT);
        Assert.fail("Should not be able to acquire lock for key: " + key);
      } catch (Exception e) {
        // expected
      }
    } catch (Exception e) {
      Assert.fail(e.getMessage(), e);
    }
  }

  @Test
  public void testReleaseLock() {
    final String key = "/TestLockClient_testReleaseLock";
    try (LockClient lockClient = createLockClient()) {
      LockInfo lockInfo = createLockInfo();
      lockClient.acquireLock(key, lockInfo, MetaClientInterface.EntryMode.PERSISTENT);
      Assert.assertNotNull(lockClient.retrieveLock(key));

      lockClient.releaseLock(key);
      Assert.assertNull(lockClient.retrieveLock(key));
      lockClient.releaseLock(TEST_INVALID_PATH);
    } catch (Exception e) {
      Assert.fail(e.getMessage(), e);
    }
  }

  @Test
  public void testAcquireTTLLock() {
    final String key = "/TestLockClient_testAcquireTTLLock";
    try (LockClient lockClient = createLockClient()) {
      LockInfo lockInfo = createLockInfo();
      lockClient.acquireLockWithTTL(key, lockInfo, 1L);
      Assert.assertNotNull(lockClient.retrieveLock(key));
      try {
        lockClient.acquireLockWithTTL(TEST_INVALID_PATH, lockInfo, 1L);
        Assert.fail("Should not be able to acquire lock for key: " + key);
      } catch (Exception e) {
        // expected
      }
    } catch (Exception e) {
      Assert.fail(e.getMessage(), e);
    }
  }

  @Test
  public void testRetrieveLock() {
    final String key = "/TestLockClient_testRetrieveLock";
    try (LockClient lockClient = createLockClient()) {
      LockInfo lockInfo = createLockInfo();
      lockClient.acquireLock(key, lockInfo, MetaClientInterface.EntryMode.PERSISTENT);
      Assert.assertNotNull(lockClient.retrieveLock(key));
      Assert.assertEquals(lockClient.retrieveLock(key).getOwnerId(), OWNER_ID);
      Assert.assertEquals(lockClient.retrieveLock(key).getClientId(), CLIENT_ID);
      Assert.assertEquals(lockClient.retrieveLock(key).getClientData(), CLIENT_DATA);
      Assert.assertEquals(lockClient.retrieveLock(key).getLockId(), LOCK_ID);
      Assert.assertEquals(lockClient.retrieveLock(key).getTimeout(), TIMEOUT);
      Assert.assertNull(lockClient.retrieveLock(TEST_INVALID_PATH));
    } catch (Exception e) {
      Assert.fail(e.getMessage(), e);
    }
  }

  @Test
  public void testRenewTTLLock() {
    final String key = "/TestLockClient_testRenewTTLLock";
    try (LockClient lockClient = createLockClient()) {
      LockInfo lockInfo = createLockInfo();
      lockClient.acquireLockWithTTL(key, lockInfo, 1L);
      Assert.assertNotNull(lockClient.retrieveLock(key));

      lockClient.renewTTLLock(key);
      Assert.assertNotSame(lockClient.retrieveLock(key).getGrantedAt(), lockClient.retrieveLock(key).getLastRenewedAt());
      try {
        lockClient.renewTTLLock(TEST_INVALID_PATH);
        Assert.fail("Should not be able to renew lock for key: " + key);
      } catch (Exception e) {
        // expected
      }
    } catch (Exception e) {
      Assert.fail(e.getMessage(), e);
    }
  }
}
