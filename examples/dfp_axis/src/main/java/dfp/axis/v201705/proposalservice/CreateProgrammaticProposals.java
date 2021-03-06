// Copyright 2016 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package dfp.axis.v201705.proposalservice;

import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.v201705.Money;
import com.google.api.ads.dfp.axis.v201705.NetworkServiceInterface;
import com.google.api.ads.dfp.axis.v201705.Proposal;
import com.google.api.ads.dfp.axis.v201705.ProposalCompanyAssociation;
import com.google.api.ads.dfp.axis.v201705.ProposalCompanyAssociationType;
import com.google.api.ads.dfp.axis.v201705.ProposalMarketplaceInfo;
import com.google.api.ads.dfp.axis.v201705.ProposalServiceInterface;
import com.google.api.ads.dfp.axis.v201705.SalespersonSplit;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.auth.oauth2.Credential;
import java.util.Random;

/**
 * This example creates a programmatic proposal. Your network must have
 * sales management enabled to run this example.
 *
 * <p>Credentials and properties in {@code fromFile()} are pulled from the
 * "ads.properties" file. See README for more info.
 */
public class CreateProgrammaticProposals {
  
  // Set the ID of the advertiser that the proposal will belong to.
  private static final String ADVERTISER_ID = "INSERT_ADVERTISER_ID_HERE";

  // Set the ID of the buyer. This can be obtained through the Programmatic_Buyer PQL table.
  private static final String BUYER_ID = "INSERT_BUYER_ID_HERE";

  // Set the ID of the primary salesperson.
  private static final String PRIMARY_SALESPERSON_ID = "INSERT_PRIMARY_SALESPERSON_ID_HERE";

  // Set the ID of the primary trafficker.
  private static final String PRIMARY_TRAFFICKER_ID = "INSERT_PRIMARY_TRAFFICKER_ID_HERE";
  
  public static void runExample(DfpServices dfpServices, DfpSession session,
      long advertiserId, long buyerId, long primarySalespersonId,
      long primaryTraffickerId) throws Exception {
    // Get the ProposalService.
    ProposalServiceInterface proposalService =
        dfpServices.get(session, ProposalServiceInterface.class);

    // Get the NetworkService.
    NetworkServiceInterface networkService =
        dfpServices.get(session, NetworkServiceInterface.class);
    
    // Create a proposal.
    Proposal proposal = new Proposal();
    proposal.setName("Programmatic proposal #" + new Random().nextInt(Integer.MAX_VALUE));

    // Set the required Marketplace information.
    ProposalMarketplaceInfo marketplaceInfo = new ProposalMarketplaceInfo();
    marketplaceInfo.setBuyerAccountId(buyerId);
    proposal.setMarketplaceInfo(marketplaceInfo);
    proposal.setIsProgrammatic(true);

    // Create a proposal company association.
    ProposalCompanyAssociation proposalCompanyAssociation = new ProposalCompanyAssociation();
    proposalCompanyAssociation.setCompanyId(advertiserId);
    proposalCompanyAssociation.setType(ProposalCompanyAssociationType.ADVERTISER);
    proposal.setAdvertiser(proposalCompanyAssociation);

    // Create salesperson split for the primary salesperson.
    SalespersonSplit primarySalesperson = new SalespersonSplit();
    primarySalesperson.setSplit(100000);
    primarySalesperson.setUserId(primarySalespersonId);
    proposal.setPrimarySalesperson(primarySalesperson);

    // Set the trafficker.
    proposal.setPrimaryTraffickerId(primaryTraffickerId);

    // Set the probability to close to 100%.
    proposal.setProbabilityOfClose(100000L);

    // Create a budget for the proposal worth 100 in the network local currency.
    Money budget = new Money();
    budget.setMicroAmount(100000000L);
    budget.setCurrencyCode(networkService.getCurrentNetwork().getCurrencyCode());
    proposal.setBudget(budget);

    // Create the proposal on the server.
    Proposal[] proposals = proposalService.createProposals(new Proposal[] {proposal});

    for (Proposal createdProposal : proposals) {
      System.out.printf("A programmatic proposal with ID %d and name '%s' was created.%n",
          createdProposal.getId(), createdProposal.getName());
    }
  }

  public static void main(String[] args) throws Exception {
    // Generate a refreshable OAuth2 credential.
    Credential oAuth2Credential = new OfflineCredentials.Builder()
        .forApi(Api.DFP)
        .fromFile()
        .build()
        .generateCredential();

    // Construct a DfpSession.
    DfpSession session = new DfpSession.Builder()
        .fromFile()
        .withOAuth2Credential(oAuth2Credential)
        .build();

    DfpServices dfpServices = new DfpServices();

    runExample(dfpServices, session, Long.parseLong(ADVERTISER_ID),
        Long.parseLong(BUYER_ID), Long.parseLong(PRIMARY_SALESPERSON_ID),
        Long.parseLong(PRIMARY_TRAFFICKER_ID));
  }
}
