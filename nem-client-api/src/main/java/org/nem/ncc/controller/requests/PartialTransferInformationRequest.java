package org.nem.ncc.controller.requests;

import org.nem.core.model.Address;
import org.nem.core.model.primitive.Amount;
import org.nem.core.serialization.*;
import org.nem.ncc.wallet.WalletName;

/**
 * A request to get information about a partially created transfer.
 */
public class PartialTransferInformationRequest {
	private final Address recipientAddress;
	private final Amount amount;
	private final String message;
	private final boolean shouldEncrypt;

	/**
	 * Creates a new partial transfer information request.
	 */
	public PartialTransferInformationRequest(
			final Address recipientAddress,
			final Amount amount,
			final String message,
			final boolean shouldEncrypt) {
		this.recipientAddress = recipientAddress;
		this.amount = amount;
		this.message = message;
		this.shouldEncrypt = shouldEncrypt;
	}

	/**
	 * Deserializes a partial transfer information request.
	 *
	 * @param deserializer The deserializer.
	 */
	public PartialTransferInformationRequest(final Deserializer deserializer) {
		this.recipientAddress = Address.readFromOptional(deserializer, "recipient", AddressEncoding.COMPRESSED);
		this.amount = Amount.readFrom(deserializer, "amount");
		this.message = deserializer.readOptionalString("message");

		final Integer shouldEncrypt = deserializer.readOptionalInt("encrypt");
		this.shouldEncrypt = null != shouldEncrypt && 0 != shouldEncrypt;
	}

	/**
	 * Gets the recipient id.
	 *
	 * @return The recipient id.
	 */
	public Address getRecipientAddress() {
		return this.recipientAddress;
	}

	/**
	 * Gets the message.
	 *
	 * @return The message.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Gets the amount.
	 *
	 * @return The amount.
	 */
	public Amount getAmount() {
		return this.amount;
	}

	/**
	 * Gets a value indicating whether or not the payload should be encrypted.
	 *
	 * @return true if the payload should be encrypted.
	 */
	public boolean shouldEncrypt() {
		return this.shouldEncrypt;
	}
}
