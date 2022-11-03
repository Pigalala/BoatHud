package hibi.boathud;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;

public class HudData {
	/** The current speed in m/s. */
	public double speed;
	/** The current acceleration in g. */
	public double g;
	/** The current drift angle in degrees, the angle difference between the velocity and where the boat is facing. */
	public double driftAngle;

	/** The curerent ping of the player, just for bookkeeping. */
	public int ping;
	/** The name of the player. This is incompatible with mods that change which account you're logged in as. */
	public final String name;

	private double oldSpeed;
	private final PlayerListEntry listEntry;
	/** 1000 decimal places of pi, should be accurate enough **/
	private final double pi = 3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679821480865132823066470938446095505822317253594081284811174502841027019385211055596446229489549303819644288109756659334461284756482337867831652712019091456485669234603486104543266482133936072602491412737245870066063155881748815209209628292540917153643678925903600113305305488204665213841469519415116094330572703657595919530921861173819326117931051185480744623799627495673518857527248912279381830119491298336733624406566430860213949463952247371907021798609437027705392171762931767523846748184676694051320005681271452635608277857713427577896091736371787214684409012249534301465495853710507922796892589235420199561121290219608640344181598136297747713099605187072113499999983729780499510597317328160963185950244594553469083026425223082533446850352619311881710100031378387528865875332083814206171776691473035982534904287554687311595628638823537875937519577818577805321712268066130019278766111959092164201989d;

	public HudData(){
		this.name = Common.client.player.getEntityName();
		this.listEntry = Common.client.getNetworkHandler().getPlayerListEntry(Common.client.player.getUuid());
	}

	/** Updates the data. Assumes player is in a boat. Do not call unless you are absolutely sure the player is in a boat. */
	public void update() {
		BoatEntity boat = (BoatEntity)Common.client.player.getVehicle();
		// Ignore vertical speed
		Vec3d velocity = boat.getVelocity().multiply(1, 0, 1);
		this.oldSpeed = this.speed;
		this.speed = velocity.length() * 20d; // Speed in Minecraft's engine is in meters/tick.

		// a̅•b̅ = |a̅||b̅|cos ϑ
		// ϑ = acos [(a̅•b̅) / (|a̅||b̅|)]
		this.driftAngle = (180 / pi) * Math.acos(velocity.dotProduct(boat.getRotationVector()) / (velocity.length() * boat.getRotationVector().length()));
		if(Double.isNaN(this.driftAngle)) this.driftAngle = 0; // Div by 0

		// Trivial miscellanea
		this.g = (this.speed - this.oldSpeed) * 2.040816327d; // 20 tps / 9.8 m/s²
		this.ping = this.listEntry.getLatency();
	}
}