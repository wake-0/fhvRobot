package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientNameController {

	private List<String> usedNames;

	public ClientNameController() {
		usedNames = new ArrayList<>();
	}

	public boolean isNameUsed(String name) {
		return usedNames.contains(name);
	}

	public boolean registerName(String name) {
		if (isNameUsed(name)) {
			return false;
		} else {
			usedNames.add(name);
			return true;
		}
	}

	public String findSimilarName(String duplicatedName) {
		if (!isNameUsed(duplicatedName)) {
			return duplicatedName;
		} else {
			String result = duplicatedName;

			for (char a = 'a'; a <= 'z'; a++) {
				for (int i = 1; i < 100; i++) {
					if (!isNameUsed(result + i)) {
						return result + 1;
					}
				}

				result += a;
			}
		}
		return UUID.randomUUID().toString();
	}
}
