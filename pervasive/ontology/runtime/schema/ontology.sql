CREATE TABLE onto_ontologies (
	ontology_name 	varchar(50) primary key
)

CREATE TABLE onto_nodes (
	id		bigint identity primary key,
	ontology_name	varchar(50),
	label		varchar(100),
)

CREATE INDEX onto_nodesIDIndex ON onto_nodes (id)
CREATE INDEX onto_nodesLabelIndex ON onto_nodes (label, ontology_name)

CREATE TABLE onto_nodeProps (
	id 	bigint,
	name	varchar(25),
	value	varchar(100),
	primary key (id, name)
)

CREATE INDEX onto_nodePropsIDIndex ON onto_nodeProps (id)

CREATE TABLE onto_edges (
	id		bigint identity primary key,
	start_id	bigint,
	end_id		bigint,
)

CREATE INDEX onto_edgesStartIDIndex ON onto_edges (start_id)
CREATE INDEX onto_edgesEndIDIndex ON onto_edges (end_id)

CREATE TABLE onto_edgeProps (
	id	bigint,
	name	varchar(25),
	value	varchar(100),
	primary key (id, name)
)

CREATE INDEX onto_edgeProps ON onto_edgeProps (id)