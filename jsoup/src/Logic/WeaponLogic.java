package Logic;

import Entity.Objects;
import Main.Display;

public class WeaponLogic {
	Display d;
	public WeaponLogic(Display d) {
		this.d = d;
	}
	public void ThrowFlashBang() {
		long checktime = System.currentTimeMillis();
		if((checktime - d.flashtime) > (5000)){
			d.screen.bullets.add(new Objects(d.x,d.y,d.z,d));
			d.screen.bullets.get(d.screen.bullets.size()-1).UseFlashMechanism(d.rotationsin, d.rotationcos, d.rotationy);
			d.flashtime = System.currentTimeMillis();
			d.FlashAmmo--;
		}
	}
	public void Circle(double timedelay) {
		long checktime = System.currentTimeMillis();
		if((checktime - d.guntime) > (timedelay * 1000)){
			for(int i = 0; i < 180; i++){
				if(d.WeaponAmmo > 0){
					d.screen.bullets.add(new Objects(d.x,d.y,d.z,d));
					d.screen.bullets.get(d.screen.bullets.size()-1).UseBulletMechanism(Math.sin(i), Math.cos(i), 1);
					d.WeaponAmmo--;
				}
			}
			d.guntime = System.currentTimeMillis();
			d.accuracy += d.recoil;
			d.PlaySound(d.getCurrentWeapon().filepath);
			d.PlaySound("/Audio/whiz.wav");
			d.getCurrentWeapon().remainingammo = d.WeaponAmmo;
		}
	}
	public void SemiAutoFire(double timedelay) {
		if(d.canfire){
			long checktime = System.currentTimeMillis();
			if((checktime - d.guntime) > (timedelay * 1000)){
				d.screen.bullets.add(new Objects(d.x,d.y,d.z,d));
				d.screen.bullets.get(d.screen.bullets.size()-1).UseBulletMechanism(d.rotationsin, d.rotationcos, d.rotationy);
				d.guntime = System.currentTimeMillis();
			d.canfire = false;
			d.accuracy += d.recoil;
			d.PlaySound(d.getCurrentWeapon().filepath);
			d.PlaySound("/Audio/whiz.wav");
			d.WeaponAmmo--;
			d.getCurrentWeapon().remainingammo = d.WeaponAmmo;
			}
		}
	}
	public void FullAutoFire(double timedelay) {
		long checktime = System.currentTimeMillis();
		if((checktime - d.guntime) > (timedelay * 1000)){
			d.screen.bullets.add(new Objects(d.x,d.y,d.z,d));
			d.screen.bullets.get(d.screen.bullets.size()-1).UseBulletMechanism(d.rotationsin, d.rotationcos, d.rotationy);
			d.guntime = System.currentTimeMillis();
			d.accuracy += d.recoil;
			d.PlaySound(d.getCurrentWeapon().filepath);
			//PlaySound("/Audio/whiz.wav");
			d.WeaponAmmo--;
			d.getCurrentWeapon().remainingammo = d.WeaponAmmo;
		}
	}
	public void ShotgunFullFire(double timedelay) {
		long checktime = System.currentTimeMillis();
		if((checktime - d.guntime) > (timedelay * 1000)){
			for(int i = 0; i < d.firemode; i++){
				d.screen.bullets.add(new Objects(d.x,d.y,d.z,d));
				d.screen.bullets.get(d.screen.bullets.size()-1).UseBulletMechanism(d.rotationsin, d.rotationcos, d.rotationy);
			}
			d.guntime = System.currentTimeMillis();
			d.accuracy += d.recoil;
			d.PlaySound(d.getCurrentWeapon().filepath);
			d.PlaySound("/Audio/whiz.wav");
			Display.WeaponAmmo--;
			d.getCurrentWeapon().remainingammo = d.WeaponAmmo;
		}
	}	
	public void ShotgunSemiFire(double timedelay) {
		if(d.canfire){
			long checktime = System.currentTimeMillis();
			if((checktime - d.guntime) > (timedelay * 1000)){
			for(int i = 0; i < Display.firemode; i++){
				d.screen.bullets.add(new Objects(d.x,d.y,d.z,d));
				d.screen.bullets.get(d.screen.bullets.size()-1).UseBulletMechanism(d.rotationsin, d.rotationcos, d.rotationy);
			}
			d.guntime = System.currentTimeMillis();
			d.canfire = false;
			Display.accuracy += d.recoil;
			Display.PlaySound(d.getCurrentWeapon().filepath);
			Display.PlaySound("/Audio/whiz.wav");
			Display.WeaponAmmo--;
			d.getCurrentWeapon().remainingammo = d.WeaponAmmo;
			}
		}
	}
}
