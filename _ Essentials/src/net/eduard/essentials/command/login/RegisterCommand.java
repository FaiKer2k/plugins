package net.eduard.essentials.command.login;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import net.eduard.api.config.Config;
import net.eduard.api.config.ConfigSection;
import net.eduard.api.lib.core.Mine;
import net.eduard.api.lib.game.Title;
import net.eduard.api.lib.manager.CommandManager;

public class RegisterCommand extends CommandManager {
	public String messageError = "�cVoce ja esta registrado!";
	public String messageTimeOut = "�cVoce demorou para Registrar!";
	public String messageConfirm = "�cConfirme a senha!";
	public String messageConfirmError = "�cConfirmou a senha errada!";
	public String messageOnlyNumbers = "�cS� pode ter numeros na senha!";
	public String messageOnlyLetters = "�cS� pode ter letras na senha!";
	public String messageRegister = "�aPor favor se registre no servidor /register";
	public String message = "�aVoce foi registrado com sucesso!";
	public String messageMaxPasswordSize = "�cA senha esta muito grande!";
	public String messageMinPasswordSize = "�cA senha esta muito pequena!";
	public Config config = new Config("auth.yml");
	public int maxTimeToRegister = 30;
	public int minPasswordSize = 4;
	public int maxPasswordSize = 12;
	public boolean canUseNumbersOnPassword = true;
	public boolean canUseLettersOnPassword = true;
	public Title title = new Title(20, 20 * 60, 20, "�cAutentica��o",
			"�c/register <senha>");
	public Title titleSuccess = new Title(20, 20 * 3, 20, "�a�lRegistrado!",
			"�cNao use senhas faceis!");
	public boolean onRegisterAutoLogin = true;

	public boolean needCofirmPassword = true;

	private static RegisterCommand registerCommand = null;

	public static RegisterCommand getRegister() {
		return registerCommand;
	}

	public boolean isRegistered(Player p) {
		return config.contains(p.getUniqueId().toString() + ".password");
	}
	public void register(Player p, String password) {
		ConfigSection sec = config.getSection(p.getUniqueId().toString());
		sec.set("password", password);
		sec.set("name", p.getName());
		sec.set("registred-since", Mine.getNow());
	}
	public boolean canRegister(Player p, String pass) {
		if (pass.length() < minPasswordSize) {
			Mine.chat(p, messageMinPasswordSize);
			Mine.SOUND_ERROR.create(p);
		} else if (pass.length() > maxPasswordSize) {
			Mine.chat(p, messageMaxPasswordSize);
			Mine.SOUND_ERROR.create(p);
		} else {
			if (canUseLettersOnPassword&!canUseNumbersOnPassword) {
				if (pass.matches("[a-zA-Z]+")) {
					Mine.chat(p, messageOnlyLetters);
					Mine.SOUND_ERROR.create(p);
					return true;
				}
			} else if (canUseNumbersOnPassword&!canUseLettersOnPassword) {
				if (pass.matches("[0-9]+")) {
					Mine.chat(p, messageOnlyLetters);
					Mine.SOUND_ERROR.create(p);
					return true;
				}
			}else {
				return true;
			}


		}

		return false;
	}

	public RegisterCommand() {
		super("register");
		registerCommand = this;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (Mine.onlyPlayer(sender)) {
			Player p = (Player) sender;
			if (args.length == 0) {
				sendUsage(sender);
			} else {
				if (isRegistered(p)) {
					Mine.chat(p, messageError);
					Mine.SOUND_ERROR.create(p);
				} else {
					String pass = args[0];
					if (canRegister(p, pass)) {

						if (needCofirmPassword) {
							if (args.length >= 2) {
								if (!pass.equals(args[1])) {
									Mine.chat(p, messageConfirmError);
									return true;
								}
							} else {
								Mine.chat(sender, messageConfirm);
								return true;
							}
						}
						Mine.chat(p, message);
						register(p, pass);

						if (onRegisterAutoLogin) {
							p.chat("/login " + pass);
						} else {
							titleSuccess.create(p);
							Mine.SOUND_SUCCESS.create(p);
						}
					}

				}
			}
		}

		return true;
	}
	@EventHandler
	public void event(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setAllowFlight(true);
		p.setFlying(true);
		if (!isRegistered(p)) {
			Mine.chat(p, messageRegister);
			title.create(p);
			Mine.TIME.delay(maxTimeToRegister * 20, new Runnable() {

				@Override
				public void run() {
					if (!isRegistered(p)) {
						p.kickPlayer(messageTimeOut);
					}
				}
			});
		}

	}

}
