import * as d3 from "d3";
import {ClusterLayout, HierarchyNode, HierarchyPointNode} from "d3";

export interface GraphData {
  nodes: Node[];
  links: Link[];
}

interface Node {
  id: string;
  group: number;
  targets: any[]; /* incoming data does not have this field, the `toChartData` function sets this value. */
}

interface Link {
  source: String;
  target: String;
  value: number;
}

function bilink(root: any): any {
  const map: Map<string, HierarchyNode<any>> = new Map(root.leaves().map((d: any) => [d.data.id, d]));
  for (const d of root.leaves()) {
    d.dependents = [];
    d.dependencies = d.data.targets.map((i: any) => [d, map.get(i)]);
  }

  for (const d of root.leaves()) {
    for (const o of d.dependencies) {
      o[1].dependents.push(o);
    }
  }

  return root;
}

function toChartData(graphData: GraphData): any {
  const {nodes, links} = graphData;
  const groupById = new Map();
  const nodeById = new Map(nodes.map(node => [node.id, node]));

  for (const node of nodes) {
    let group = groupById.get(node.group);
    if (!group) {
      groupById.set(node.group, group = {id: node.group, children: []});
    }
    group.children.push(node);
    node.targets = [];
  }

  for (const {source: sourceId, target: targetId} of links) {
    let sourceNode = nodeById.get(sourceId.toString());
    if (sourceNode) {
      sourceNode.targets.push(targetId);
    }
  }

  return {children: [...groupById.values()]};
}

function tree(radius: number): ClusterLayout<any> {
  return d3.cluster()
    .size([2 * Math.PI, radius - 100]);
}

export function createRoot(radius: number, graphData: GraphData): HierarchyPointNode<any> {
  return tree(radius)(bilink(d3.hierarchy(toChartData(graphData))));
}