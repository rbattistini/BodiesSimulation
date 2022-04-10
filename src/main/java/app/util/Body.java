package app.util;

public class Body {
    
	private static final double REPULSIVE_CONST = 0.01;
	private static final double FRICTION_CONST = 1;
    private final P2d pos;
    private final V2d vel;
    private final double mass;
    private final int id;

    public Body(Body b) {
        this.id = b.id;
        this.pos = new P2d(b.pos);
        this.vel = new V2d(b.vel);
        this.mass = b.mass;
    }

    public Body(int id, P2d pos, V2d vel, double mass){
    	this.id = id;
        this.pos = pos;
        this.vel = vel;
        this.mass = mass;
    }
    
    public double getMass() {
    	return mass;
    }
    
    public P2d getPos(){
        return pos;
    }

    public boolean equals(Object b) {
    	return ((Body)b).id == id;
    }
    
    /**
     * Update the position, according to current velocity
     * 
     * @param dt time elapsed 
     */
    public void updatePos(double dt){    	
    	pos.sum(new V2d(vel).scalarMul(dt));
    }

    /**
     * Update the velocity, given the instant acceleration
     * @param acc instant acceleration
     * @param dt time elapsed
     */
    public void updateVelocity(V2d acc, double dt){    	
    	vel.sum(new V2d(acc).scalarMul(dt));
    }

    /**
     * Computes the distance from the specified body
     */
    public double getDistanceFrom(Body b) {
    	double dx = pos.getX() - b.getPos().getX();
    	double dy = pos.getY() - b.getPos().getY();
    	return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Compute the repulsive force exerted by another body
     */
    public V2d computeRepulsiveForceBy(Body b) throws InfiniteForceException {
		double dist = getDistanceFrom(b);
		if (dist > 0) {
			try {
				return new V2d(b.getPos(), pos)
					.normalize()
					.scalarMul(b.getMass()*REPULSIVE_CONST/(dist*dist));
			} catch (Exception ex) {
				throw new InfiniteForceException();
			}
		} else {
			throw new InfiniteForceException();
		}
    }
    
    /**
     * 
     * Compute current friction force, given the current velocity
     */
    public V2d getCurrentFrictionForce() {
        return new V2d(vel).scalarMul(-FRICTION_CONST);
    }
    
    /**
     * Check if there are collisions with the boundary and update the
     * position and velocity accordingly
     */
    public void checkAndSolveBoundaryCollision(Boundary bounds){
    	double x = pos.getX();
    	double y = pos.getY();    	
        
    	if (x > bounds.getX1()){
            pos.change(bounds.getX1(), pos.getY());
            vel.change(-vel.getX(), vel.getY());
        } else if (x < bounds.getX0()){
            pos.change(bounds.getX0(), pos.getY());
            vel.change(-vel.getX(), vel.getY());
        } 
        
        if (y > bounds.getY1()){
            pos.change(pos.getX(), bounds.getY1());
            vel.change(vel.getX(), -vel.getY());
        } else if (y < bounds.getY0()){
            pos.change(pos.getX(), bounds.getY0());
            vel.change(vel.getX(), -vel.getY());
        }
    }

    @Override
    public String toString() {
        return "Body{" +
                "pos=" + pos +
                ", vel=" + vel +
                ", mass=" + mass +
                ", id=" + id +
                '}';
    }
}
